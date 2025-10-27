package com.company.currencyexchange.view.currencyconverter;
import com.company.currencyexchange.entity.Currencies;
import com.company.currencyexchange.entity.ExchangeRates;
import com.company.currencyexchange.view.main.MainView;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;


@Route(value = "currency-converter", layout = MainView.class)
@ViewController(id = "CurrencyConverter")
@ViewDescriptor(path = "currency-converter.xml")
public class CurrencyConverter extends StandardView {

    @ViewComponent
    private JmixButton convertButton;
    @ViewComponent
    private TypedTextField<Object> count_;
    @ViewComponent
    private EntityComboBox<Currencies> from_currencieses;
    @ViewComponent
    private TypedTextField<Object> rate;
    @ViewComponent
    private TypedTextField<Object> result;
    @ViewComponent
    private EntityComboBox<Currencies> to_currencieses;
    @Autowired
    private DataManager dataManager;

    @Subscribe("from_currencieses")
    public void onFrom_currenciesesComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Currencies>, Currencies> event) {
        updateRate();
    }

    @Subscribe("to_currencieses")
    public void onTo_currenciesesComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<Currencies>, Currencies> event) {
        updateRate();
    }

    @Subscribe("convertButton")
    public void onConvertButtonClick(final ClickEvent<JmixButton> event) {
        try {
            // Получаем курс и количество
            String rateValue = rate.getValue(); // строка из textField
            String countValue = count_.getValue(); // строка из textField

            if (rateValue != null && !rateValue.isEmpty() &&
                    countValue != null && !countValue.isEmpty()) {

                double rateDouble = Double.parseDouble(rateValue);
                double countDouble = Double.parseDouble(countValue);
                double resultDouble = rateDouble * countDouble;
                result.setValue(String.valueOf(resultDouble));
            } else {
                result.clear(); // очищаем, если данные некорректные
            }
        } catch (NumberFormatException e) {
            result.clear(); // очищаем при ошибке парсинга
        }
    }



    private void updateRate() {
        Currencies base = from_currencieses.getValue();
        Currencies target = to_currencieses.getValue();

        if (base != null && target != null) {
            // Сначала ищем прямой курс
            ExchangeRates exchangeRate = dataManager.load(ExchangeRates.class)
                    .query("select e from ExchangeRates e " +
                            "where e.baseCurrencyId = :base and e.targetCurrencyId = :target " +
                            "order by e.createDate desc")
                    .parameter("base", base)
                    .parameter("target", target)
                    .optional()
                    .orElse(null);

            if (exchangeRate != null) {
                rate.setValue(exchangeRate.getRate().toString());
            } else {
                // Прямого курса нет — ищем обратный курс (target → base)
                ExchangeRates reverseRate = dataManager.load(ExchangeRates.class)
                        .query("select e from ExchangeRates e " +
                                "where e.baseCurrencyId = :target and e.targetCurrencyId = :base " +
                                "order by e.createDate desc")
                        .parameter("base", base)
                        .parameter("target", target)
                        .optional()
                        .orElse(null);

                if (reverseRate != null && reverseRate.getRate() != null && reverseRate.getRate().doubleValue() != 0) {
                    // Используем обратный курс: 1 / reverseRate
                    double inverse = 1.0 / reverseRate.getRate().doubleValue();
                    rate.setValue(String.valueOf(inverse));
                } else {
                    // Если не найден вообще
                    rate.clear();
                }
            }
        }
    }



}
