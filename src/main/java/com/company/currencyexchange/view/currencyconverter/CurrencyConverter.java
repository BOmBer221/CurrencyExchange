package com.company.currencyexchange.view.currencyconverter;

import com.company.currencyexchange.entity.Currencies;
import com.company.currencyexchange.entity.ExchangeModel;
import com.company.currencyexchange.entity.ExchangeRates;
import com.company.currencyexchange.view.main.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.component.valuepicker.EntityPicker;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Route(value = "currency-converter", layout = MainView.class)
@ViewController(id = "CurrencyConverter")
@ViewDescriptor(path = "currency-converter.xml")
public class CurrencyConverter extends StandardView {

    @ViewComponent
    private EntityPicker<Currencies> fromCurrencyField;

    @ViewComponent
    private EntityPicker<Currencies> toCurrencyField;

    @ViewComponent
    private TextField amountField;

    @ViewComponent
    private TextField rateField;

    @ViewComponent
    private TextField resultField;

    @ViewComponent
    private Button convertButton;

    @Autowired
    private DataManager dataManager;

    @Subscribe
    public void onInit(InitEvent event) {
        // обработчик кнопки
        convertButton.addClickListener(e -> calculateResult());

        // автообновление курса при изменении валют
        fromCurrencyField.addValueChangeListener(e -> updateRate());
        toCurrencyField.addValueChangeListener(e -> updateRate());
    }

    private void updateRate() {
        Currencies from = fromCurrencyField.getValue();
        Currencies to = toCurrencyField.getValue();

        if (from != null && to != null) {
            // Сначала ищем прямой курс (from → to)
            Optional<ExchangeRates> directRateOpt = dataManager.load(ExchangeRates.class)
                    .query("select e from ExchangeRates e where e.baseCurrencyId.id = :fromId and e.targetCurrencyId.id = :toId")
                    .parameter("fromId", from.getId())
                    .parameter("toId", to.getId())
                    .optional();
            if (directRateOpt.isPresent()) {
                rateField.setValue(String.valueOf(directRateOpt.get().getRate()));
                return;
            }

            // Если прямого курса нет — ищем обратный (to → from)
            Optional<ExchangeRates> reverseRateOpt = dataManager.load(ExchangeRates.class)
                    .query("select e from ExchangeRates e where e.baseCurrencyId.id = :toId and e.targetCurrencyId.id = :fromId")
                    .parameter("fromId", from.getId())
                    .parameter("toId", to.getId())
                    .optional();
            if (reverseRateOpt.isPresent()) {
                Double reverseRate = reverseRateOpt.get().getRate();
                if (reverseRate != null && reverseRate != 0) {
                    rateField.setValue(String.valueOf(1 / reverseRate));
                    return;
                }
            }

            // Если ни прямого, ни обратного нет — очищаем поле
            rateField.setValue("");
        } else {
            rateField.setValue("");
        }
    }

    private void calculateResult() {
        try {
            double amount = Double.parseDouble(amountField.getValue());
            double rate = Double.parseDouble(rateField.getValue());
            double result = amount * rate;
            resultField.setValue(String.format("%.2f", result));
        } catch (Exception ex) {
            resultField.setValue("Ошибка ввода");
        }
    }
}
