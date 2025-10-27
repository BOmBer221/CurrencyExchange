package com.company.currencyexchange.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@JmixEntity
@Table(name = "EXCHANGE_RATES", indexes = {
        @Index(name = "IDX_EXCHANGE_RATES_TARGET_CURRENCY_ID", columnList = "TARGET_CURRENCY_ID_ID")
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
    private Double rate;

    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate;

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getRate() {
        return rate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
    public LocalDateTime getCreateDate() {
        return createDate;
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