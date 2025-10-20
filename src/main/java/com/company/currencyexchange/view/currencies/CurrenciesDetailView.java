package com.company.currencyexchange.view.currencies;

import com.company.currencyexchange.entity.Currencies;
import com.company.currencyexchange.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "currencieses/:id", layout = MainView.class)
@ViewController(id = "Currencies.detail")
@ViewDescriptor(path = "currencies-detail-view.xml")
@EditedEntityContainer("currenciesDc")
public class CurrenciesDetailView extends StandardDetailView<Currencies> {
}