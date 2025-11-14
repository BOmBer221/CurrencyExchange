package com.company.currencyexchange.api;

import com.company.currencyexchange.dto.CurrencyDTO;
import com.company.currencyexchange.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Component
@RestController
@RequestMapping("/api/v1/currency")
public class CurrencyController {
    @Autowired
    private CurrencyService currencyService;

    //получить все валюты
    @GetMapping("/all")
    public ResponseEntity<List<CurrencyDTO>> getCurrencies() {
        List<CurrencyDTO> currencies= currencyService.getCurrencyDTO();
        return ResponseEntity.ok(currencies);

    }

    // получить валюту по коду
    @GetMapping("{code}")
    public ResponseEntity<CurrencyDTO> getCurrencyByCode(@PathVariable String code) {
        CurrencyDTO currency = currencyService.getCurrencyByCode(code);
        if (currency == null) {
            return ResponseEntity.notFound().build(); // 404 если не найдено
        }
        return ResponseEntity.ok(currency);           // 200 + JSON если найдено
    }

    // удаление валюты по коду
    //по идее работает, но не удаляется, так как есть связь с таблицей ExchangeRate
    @DeleteMapping("{code}")
    public ResponseEntity<CurrencyDTO> deleteCurrencyByCode(@PathVariable String code) {
        boolean deleted = currencyService.deleteCurrencyByCode(code);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
