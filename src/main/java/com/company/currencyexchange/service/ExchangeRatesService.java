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

    //мапер ДТО
    public ExchangeRatesDTO mapDTO(ExchangeRates exchangeRate) {
        ExchangeRatesDTO dto = dataManager.create(ExchangeRatesDTO.class);

        dto.setBaseCurrencyCode(exchangeRate.getBaseCurrencyId().getCode());
        dto.setTargetCurrencyCode(exchangeRate.getTargetCurrencyId().getCode());
        dto.setRate(exchangeRate.getRate());
        dto.setCreateDate(exchangeRate.getCreateDate());
        dto.setName(exchangeRate.getBaseCurrencyId().getCode() + " → " + exchangeRate.getTargetCurrencyId().getCode());

        return dto;
    }

    //JPQL запрос курс валют по коду
    public ExchangeRatesDTO getRateByCode(String baseCode,String targetCode ) {
        ExchangeRates dto = dataManager.load(ExchangeRates.class)
                .query("select e from ExchangeRates e " +
                        "where e.baseCurrencyId.code = :base " +
                        "and e.targetCurrencyId.code = :target")
                .parameter("base",baseCode)
                .parameter("target",targetCode)
                .optional()
                .orElse(null);

        return dto == null ? null : mapDTO(dto);
    }

    //JPQL запрос удаления курса валют по коду
    public boolean deleteRateByCode(String baseCode,String targetCode ) {
        ExchangeRates dto =dataManager.load(ExchangeRates.class)
                .query("select e from ExchangeRates e " +
                        "where e.baseCurrencyId.code = :base " +
                        "and e.targetCurrencyId.code = :target")
                .parameter("base", baseCode)
                .parameter("target", targetCode)
                .optional()
                .orElse(null);
        if (dto == null) {return false;}
        dataManager.remove(dto);
        return true;
    }



}
