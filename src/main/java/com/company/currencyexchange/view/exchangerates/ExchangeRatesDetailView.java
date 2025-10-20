package com.company.currencyexchange.view.exchangerates;

import com.company.currencyexchange.entity.ExchangeRates;
import com.company.currencyexchange.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "exchange-rateses/:id", layout = MainView.class)
@ViewController(id = "ExchangeRates.detail")
@ViewDescriptor(path = "exchange-rates-detail-view.xml")
@EditedEntityContainer("exchangeRatesDc")
public class ExchangeRatesDetailView extends StandardDetailView<ExchangeRates> {
}