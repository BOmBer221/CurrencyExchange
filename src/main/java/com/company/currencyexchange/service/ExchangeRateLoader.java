package com.company.currencyexchange.service;

import com.company.currencyexchange.entity.Currencies;
import com.company.currencyexchange.entity.ExchangeRates;
import io.jmix.core.DataManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.*;

import org.xml.sax.InputSource;

@Component("ExchangeRateLoader")
public class ExchangeRateLoader {

    private final DataManager dataManager;
    private final RestTemplate restTemplate = new RestTemplate();

    private String fetchCbrXml() {
        try {
            String url = "https://www.cbr.ru/scripts/XML_daily.asp";
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, ValuteInfo> parseCbrXml(String xml) {
        Map<String, ValuteInfo> map = new HashMap<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));

            NodeList valutes = doc.getElementsByTagName("Valute");
            for (int i = 0; i < valutes.getLength(); i++) {
                Node val = valutes.item(i);
                String charCode = null;
                Integer nominal = 1;
                Double value = 0.0;
                NodeList children = val.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    Node c = children.item(j);
                    String name = c.getNodeName();
                    if ("CharCode".equals(name)) {
                        charCode = c.getTextContent().trim();
                    } else if ("Nominal".equals(name)) {
                        try {
                            nominal = Integer.parseInt(c.getTextContent().trim());
                        } catch (NumberFormatException ignored) {}
                    } else if ("Value".equals(name)) {
                        String s = c.getTextContent().trim().replace(',', '.');
                        try {
                            value = Double.parseDouble(s);
                        } catch (NumberFormatException ignored) {}
                    }
                }
                if (charCode != null) {
                    map.put(charCode, new ValuteInfo(nominal, value));
                }
            }
        } catch (Exception ignored) {}
        return map;
    }

    // Список валют которые надо обновлять (целевые) — RUB является базовой
    private static final List<CurrencyMeta> TARGETS = Arrays.asList(
            new CurrencyMeta("USD", "Доллар США", "$", "США"),
            new CurrencyMeta("EUR", "Евро", "€", "Евросоюз"),
            new CurrencyMeta("CNY", "Юань", "¥", "Китай"),
            new CurrencyMeta("TRY", "Турецких лир", "₺", "Турция"),
            new CurrencyMeta("GBP", "Фунт стерлингов", "£", "Великобритания")
    );

    public ExchangeRateLoader(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    /**
     * Главный метод: загрузить курсы и сохранить/обновить ExchangeRates.
     */
    @Transactional
    public void loadAndSaveRates() {
        // 1) получаем XML от ЦБ
        String xml = fetchCbrXml();
        if (xml == null) {
            throw new RuntimeException("Не удалось получить данные от ЦБ");
        }

        // 2) парсим XML в Map<CharCode, Pair(nominal, valueInRub)>
        Map<String, ValuteInfo> xmlRates = parseCbrXml(xml);

        // 3) Обеспечиваем наличие записи RUB в Currencies (если нет — создаем)
        Currencies rub = findOrCreateCurrency("RUB", "Российский рубль", "₽", "Россия");

        LocalDateTime now = LocalDateTime.now();

        // --- сохраняем курсы RUB -> TARGET ---
        List<Currencies> targetCurrencies = new ArrayList<>();
        Map<Currencies, Double> rubRates = new HashMap<>();

        for (CurrencyMeta meta : TARGETS) {
            // ensure target currency exists (code, fullname, sign, country)
            Currencies target = findOrCreateCurrency(meta.code, meta.fullName, meta.sign, meta.country);
            targetCurrencies.add(target);

            ValuteInfo info = xmlRates.get(meta.code);
            if (info == null || info.value == 0.0) {
                continue; // если нет данных или деление на ноль
            }

            double targetPerRub = (double) info.nominal / info.value;

            // ищем последний ExchangeRates для пары (base=RUB, target=target)
            ExchangeRates existing = dataManager.load(ExchangeRates.class)
                    .query("select e from ExchangeRates e " +
                            "where e.baseCurrencyId = :base and e.targetCurrencyId = :target " +
                            "order by e.createDate desc")
                    .parameter("base", rub)
                    .parameter("target", target)
                    .optional()
                    .orElse(null);

            if (existing != null) {
                existing.setRate(targetPerRub);
                existing.setCreateDate(now);
                dataManager.save(existing);
            } else {
                ExchangeRates er = dataManager.create(ExchangeRates.class);
                er.setBaseCurrencyId(rub);
                er.setTargetCurrencyId(target);
                er.setRate(targetPerRub);
                er.setCreateDate(now);
                dataManager.save(er);
            }

            rubRates.put(target, targetPerRub); // сохраняем для кросс-курсов
        }

        // --- расчет кросс-курсов между TARGET валютами (без обратных) ---
        for (int i = 0; i < targetCurrencies.size(); i++) {
            Currencies base = targetCurrencies.get(i);
            for (int j = i + 1; j < targetCurrencies.size(); j++) {
                Currencies target = targetCurrencies.get(j);

                Double baseRate = rubRates.get(base);
                Double targetRate = rubRates.get(target);

                if (baseRate == null || targetRate == null || baseRate == 0.0) continue;

                double crossRate = targetRate / baseRate;

                // сохраняем только если курс ещё не существует
                ExchangeRates existing = dataManager.load(ExchangeRates.class)
                        .query("select e from ExchangeRates e " +
                                "where e.baseCurrencyId = :base and e.targetCurrencyId = :target")
                        .parameter("base", base)
                        .parameter("target", target)
                        .optional()
                        .orElse(null);

                if (existing != null) {
                    existing.setRate(crossRate);
                    existing.setCreateDate(now);
                    dataManager.save(existing);
                } else {
                    ExchangeRates er = dataManager.create(ExchangeRates.class);
                    er.setBaseCurrencyId(base);
                    er.setTargetCurrencyId(target);
                    er.setRate(crossRate);
                    er.setCreateDate(now);
                    dataManager.save(er);
                }
            }
        }
    }



    private Currencies findOrCreateCurrency(String code, String fullName, String sign, String country) {
        Currencies existing = dataManager.load(Currencies.class)
                .query("select c from Currencies c where c.code = :code")
                .parameter("code", code)
                .optional()
                .orElse(null);
        if (existing != null) {
            boolean changed = false;
            if (existing.getFullName() == null || existing.getFullName().isEmpty()) {
                existing.setFullName(fullName);
                changed = true;
            }
            if (existing.getSign() == null || existing.getSign().isEmpty()) {
                existing.setSign(sign);
                changed = true;
            }
            if (existing.getCountry() == null || existing.getCountry().isEmpty()) {
                existing.setCountry(country);
                changed = true;
            }
            if (changed) dataManager.save(existing);
            return existing;
        } else {
            Currencies c = dataManager.create(Currencies.class);
            c.setCode(code);
            c.setFullName(fullName);
            c.setSign(sign);
            c.setCountry(country);
            return dataManager.save(c);
        }
    }

    // ---- вспомогательные классы ----
    private static class ValuteInfo {
        int nominal;
        double value; // value in RUB for nominal units
        ValuteInfo(int nominal, double value) {
            this.nominal = nominal;
            this.value = value;
        }
    }

    private static class CurrencyMeta {
        final String code;
        final String fullName;
        final String sign;
        final String country;
        CurrencyMeta(String code, String fullName, String sign, String country) {
            this.code = code;
            this.fullName = fullName;
            this.sign = sign;
            this.country = country;
        }
    }
}
