package com.company.currencyexchange.service;

import com.company.currencyexchange.dto.ExchangeRatesDTO;
import com.company.currencyexchange.entity.ExchangeRates;
import io.jmix.core.UnconstrainedDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExchangeRatesService {
    @Autowired
    public UnconstrainedDataManager dataManager;

    // Получаем все записи курсов влют
    public List<ExchangeRates> getExchangeRates() {
        return dataManager.load(ExchangeRates.class).all().list();
    }
    //получаем весь курс в виде дто
    public List<ExchangeRatesDTO> getExchangeRatesDTO() {
        List<ExchangeRates> exchangeRates = getExchangeRates();
        List<ExchangeRatesDTO> exchangeRatesDTOs = new ArrayList<>();
        exchangeRates.stream().map(this::mapDTO).forEach(exchangeRatesDTOs::add);
        return exchangeRatesDTOs;
    }

    public ExchangeRatesDTO mapDTO(ExchangeRates exchangeRate) {
        ExchangeRatesDTO dto = dataManager.create(ExchangeRatesDTO.class);

        dto.setBaseCurrencyCode(exchangeRate.getBaseCurrencyId().getCode());
        dto.setTargetCurrencyCode(exchangeRate.getTargetCurrencyId().getCode());
        dto.setRate(exchangeRate.getRate());
        dto.setCreateDate(exchangeRate.getCreateDate());
        dto.setName(exchangeRate.getBaseCurrencyId().getCode() + " → " + exchangeRate.getTargetCurrencyId().getCode());

        return dto;
    }



}
