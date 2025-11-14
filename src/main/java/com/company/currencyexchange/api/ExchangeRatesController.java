package com.company.currencyexchange.api;

import com.company.currencyexchange.dto.ExchangeRatesDTO;
import com.company.currencyexchange.service.ExchangeRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Component
@RestController
@RequestMapping("/api/v1/exchangerates")
public class ExchangeRatesController {
    @Autowired
    private ExchangeRatesService exchangeRatesService;

    //получение всех курсов
    @GetMapping("/all")
    public ResponseEntity<List<ExchangeRatesDTO>> getAllExchangeRates() {
        List<ExchangeRatesDTO> exchangeRates = exchangeRatesService.getExchangeRatesDTO();
        return ResponseEntity.ok(exchangeRates);
    }

    //получение курса по кодам валют
    //пример запроса: http://localhost:8080/api/v1/exchangerates?base=USD&target=EUR
    @GetMapping
    public ResponseEntity<ExchangeRatesDTO> getRate(@RequestParam String base, @RequestParam String target) {
        ExchangeRatesDTO dto = exchangeRatesService.getRateByCode(base, target);
        return ResponseEntity.ok(dto);
    }

    //удаление курса по коду валют
    @DeleteMapping
    public ResponseEntity<ExchangeRatesDTO> deleteRate(
            @RequestParam String base,
            @RequestParam String target) {

        boolean deleted = exchangeRatesService.deleteRateByCode(base, target);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build(); // 204 No Content
    }

}
