package com.company.currencyexchange.service;

import com.company.currencyexchange.dto.CurrencyDTO;
import com.company.currencyexchange.entity.Currencies;
import com.company.currencyexchange.entity.ExchangeRates;
import io.jmix.core.UnconstrainedDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;

@Component
public class CurrencyService {
    @Autowired
    public UnconstrainedDataManager dataManager;

    public List<Currencies> getCurrencies () {
        return dataManager.load(Currencies.class).all().list();
    }

    // получение всех валют в виде списка
    public List<CurrencyDTO> getCurrencyDTO() {
        List<Currencies> currencies = getCurrencies();
        List<CurrencyDTO> currencyDTOs = new ArrayList<>();
        currencies.stream().map(this::mapDTO).forEach(currencyDTOs::add);
        return currencyDTOs;
    }

    //мапер ДТО
    public CurrencyDTO mapDTO(Currencies currency) {
        CurrencyDTO currencyDTO = dataManager.create(CurrencyDTO.class);
        currencyDTO.setName(currency.getFullName());
        currencyDTO.setCode(currency.getCode());
        currencyDTO.setSign(currency.getSign());
        return currencyDTO;
    }

    // JPQL запрос валюты по коду
    public CurrencyDTO getCurrencyByCode(String code) {
        Currencies currencies = dataManager.load(Currencies.class)
                .query("select c from Currencies c where c.code = :code")
                .parameter("code", code)
                .optional()
                .orElse(null);
        //обработчик ошибки
        if (currencies == null) {
            throw new IllegalArgumentException("Валюта с кодом: "+code+" не найдена");
        }
        return currencies == null?null: mapDTO(currencies);
    }

    // удаление валюты по коду
    //по идее работает, но не удаляется, так как есть связь с таблицей ExchangeRate
    public boolean deleteCurrencyByCode(String code) {

        // Загружаем валюту
        Currencies currency = dataManager.load(Currencies.class)
                .query("select c from Currencies c where c.code = :code")
                .parameter("code", code)
                .optional()
                .orElse(null);
        //обработчик ошибок
        if (currency == null) {
            throw new IllegalArgumentException("Валюта с кодом: "+code+" не найдена");
        }

        // Проверяем связи с таблицей ExchangeRates
        List<ExchangeRates> related = dataManager.load(ExchangeRates.class)
                .query("select e from ExchangeRates e " +
                        "where e.baseCurrencyId = :cur or e.targetCurrencyId = :cur")
                .parameter("cur", currency)
                .list();

        // Есть связанные курсы -> выбрасываем исключение
        if (!related.isEmpty()) {
            throw new IllegalStateException(
                    "Невозможно удалить валюту " + code + " — существуют связанные курсы обмена"
            );
        }

        // Удаляем валюту
        dataManager.remove(currency);
        return true;
    }

}

