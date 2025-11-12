package com.company.currencyexchange.service;

import com.company.currencyexchange.dto.CurrencyDTO;
import com.company.currencyexchange.entity.Currencies;
import io.jmix.core.UnconstrainedDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;

@Component
public class CurrencyService {
    @Autowired
    public UnconstrainedDataManager dataManager;

    public List<CurrencyDTO> getCurrencyDTO() {
        List<Currencies> currencies = getCurrencies();
        List<CurrencyDTO> currencyDTOs = new ArrayList<>();
        currencies.stream().map(this::mapDTO).forEach(currencyDTOs::add);
        return currencyDTOs;
    }

    public List<Currencies> getCurrencies () {
        return dataManager.load(Currencies.class).all().list();
    }

    public CurrencyDTO mapDTO(Currencies currency) {
        CurrencyDTO currencyDTO = dataManager.create(CurrencyDTO.class);
        currencyDTO.setName(currency.getFullName());
        currencyDTO.setCode(currency.getCode());
        currencyDTO.setSign(currency.getSign());
        return currencyDTO;
    }
}

