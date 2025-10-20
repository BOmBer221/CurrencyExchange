package com.company.currencyexchange.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;

import java.util.UUID;

@JmixEntity
@Table(name = "EXCHANGE_RATES", indexes = {
        @Index(name = "IDX_EXCHANGE_RATES_TARGET_CURRENCY_ID", columnList = "TARGET_CURRENCY_ID_ID"),
        @Index(name = "IDX_EXCHANGE_RATES_BASE_CURRENCY_ID", columnList = "")
})
@Entity
public class ExchangeRates {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @JoinColumn(name = "BASE_CURRENCY_ID_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Currencies baseCurrencyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TARGET_CURRENCY_ID_ID")
    private Currencies targetCurrencyId;

    @Column(name = "RATE")
    private Integer rate;

    @Column(name = "CREATE_DATE")
    private String createDate;

    public String getCreateDate() {
        return createDate;
    }
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Integer getRate() {
        return rate;
    }
    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public void setBaseCurrencyId(Currencies baseCurrencyId) {
        this.baseCurrencyId = baseCurrencyId;
    }
    public Currencies getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public Currencies getTargetCurrencyId() {
        return targetCurrencyId;
    }
    public void setTargetCurrencyId(Currencies targetCurrencyId) {
        this.targetCurrencyId = targetCurrencyId;
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

}