package com.company.currencyexchange.view.exchangerates;

import com.company.currencyexchange.entity.ExchangeRates;
import com.company.currencyexchange.service.ExchangeRateLoader;
import com.company.currencyexchange.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;

import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "exchange-rateses", layout = MainView.class)
@ViewController(id = "ExchangeRates.list")
@ViewDescriptor(path = "exchange-rates-list-view.xml")
@LookupComponent("exchangeRatesesDataGrid")
@DialogMode(width = "64em")
public class ExchangeRatesListView extends StandardListView<ExchangeRates> {

    @Autowired
    private ExchangeRateLoader exchangeRateLoader;

    @ViewComponent
    private CollectionLoader<ExchangeRates> exchangeRatesesDl;

    @Subscribe(id = "updateButton", subject = "clickListener")
    public void onUpdateButtonClick(final ClickEvent<JmixButton> event) {
        try {
            exchangeRateLoader.loadAndSaveRates();
            exchangeRatesesDl.load(); // обновляем таблицу
            Notification.show("Курсы обновлены").setDuration(3000);
        } catch (Exception e) {
            Notification.show("Ошибка при обновлении курсов").setDuration(3000);
        }
    }

}
