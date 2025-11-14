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

    public List<Currencies> getCurrencies () {
        return dataManager.load(Currencies.class).all().list();
    }

    // получение всех валют в виде списка
    public List<CurrencyDTO> getCurrencyDTO() {
        List<Currencies> currencies = getCurrencies();
        List<CurrencyDTO> currencyDTOs = new ArrayList<>();
        currencies.stream().map(this::mapDTO).forEach(currencyDTOs::add);
        return currencyDTOs;
    }

    //мапер ДТО
    public CurrencyDTO mapDTO(Currencies currency) {
        CurrencyDTO currencyDTO = dataManager.create(CurrencyDTO.class);
        currencyDTO.setName(currency.getFullName());
        currencyDTO.setCode(currency.getCode());
        currencyDTO.setSign(currency.getSign());
        return currencyDTO;
    }

    // JPQL запрос валюты по коду
    public CurrencyDTO getCurrencyByCode(String code) {
        Currencies currencies = dataManager.load(Currencies.class)
                .query("select c from Currencies c where c.code = :code")
                .parameter("code", code)
                .optional()
                .orElse(null);

        return currencies == null?null: mapDTO(currencies);
    }

    // удаление валюты по коду
    //по идее работает, но не удаляется, так как есть связь с таблицей ExchangeRate
    public boolean deleteCurrencyByCode(String code) {
        Currencies currencies = dataManager.load(Currencies.class)
                .query("select c from Currencies c " +
                        "where c.code = :code")
                .parameter("code", code)
                .optional()
                .orElse(null);
        if (currencies == null) {return false;}
        dataManager.remove(currencies);
        return true;
    }

}

