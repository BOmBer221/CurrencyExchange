package com.company.currencyexchange.api;

import com.company.currencyexchange.dto.ExchangeRatesDTO;
import com.company.currencyexchange.service.ExchangeRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Component
@RestController
@RequestMapping("/api/v1/exchangerates")
public class ExchangeRatesController {
    @Autowired
    private ExchangeRatesService exchangeRatesService;


    @GetMapping("/all")
    public ResponseEntity<List<ExchangeRatesDTO>> getAllExchangeRates() {
        List<ExchangeRatesDTO> exchangeRates = exchangeRatesService.getExchangeRatesDTO();
        return ResponseEntity.ok(exchangeRates);
    }
}
