package com.company.currencyexchange.view.exchangerates;

import com.company.currencyexchange.entity.ExchangeRates;
import com.company.currencyexchange.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "exchange-rateses", layout = MainView.class)
@ViewController(id = "ExchangeRates.list")
@ViewDescriptor(path = "exchange-rates-list-view.xml")
@LookupComponent("exchangeRatesesDataGrid")
@DialogMode(width = "64em")
public class ExchangeRatesListView extends StandardListView<ExchangeRates> {
}