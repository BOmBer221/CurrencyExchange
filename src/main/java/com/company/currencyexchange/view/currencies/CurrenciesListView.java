package com.company.currencyexchange.view.currencies;

import com.company.currencyexchange.entity.Currencies;
import com.company.currencyexchange.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "currencieses", layout = MainView.class)
@ViewController(id = "Currencies.list")
@ViewDescriptor(path = "currencies-list-view.xml")
@LookupComponent("currenciesesDataGrid")
@DialogMode(width = "64em")
public class CurrenciesListView extends StandardListView<Currencies> {
}