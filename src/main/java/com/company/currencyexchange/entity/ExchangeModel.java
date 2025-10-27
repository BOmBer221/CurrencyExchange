package com.company.currencyexchange.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.JmixId;
import io.jmix.core.metamodel.annotation.JmixEntity;

import java.util.UUID;

@JmixEntity
public class ExchangeModel {
    @JmixGeneratedValue
    @JmixId
    private UUID id;
    private Currencies fromCurrency;
    private Currencies toCurrency;
    private Double amount;
    private Double result;

    public Double getResult() {return result;}
    public void setResult(Double result) {this.result = result;}

    public Double getAmount() {return amount;}
    public void setAmount(Double amount) {this.amount = amount;}

    public Currencies getToCurrency() {return toCurrency;}
    public void setToCurrency(Currencies toCurrency) {this.toCurrency = toCurrency;}

    public Currencies getFromCurrency() {return fromCurrency;}

    public void setFromCurrency(Currencies fromCurrency) {this.fromCurrency = fromCurrency;}

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

}