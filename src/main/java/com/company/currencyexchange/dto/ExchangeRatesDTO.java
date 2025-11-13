package com.company.currencyexchange.dto;

import io.jmix.core.metamodel.annotation.JmixEntity;

import java.time.LocalDateTime;

@JmixEntity
public class ExchangeRatesDTO {

    private String name; // например "USD → EUR"

    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private Double rate;
    private LocalDateTime createDate;


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getBaseCurrencyCode() {
        return baseCurrencyCode;
    }
    public void setBaseCurrencyCode(String baseCurrencyCode) {
        this.baseCurrencyCode = baseCurrencyCode;
    }

    public String getTargetCurrencyCode() {
        return targetCurrencyCode;
    }
    public void setTargetCurrencyCode(String targetCurrencyCode) {
        this.targetCurrencyCode = targetCurrencyCode;
    }

    public Double getRate() {
        return rate;
    }
    public void setRate(Double rate) {
        this.rate = rate;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

}