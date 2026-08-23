package com.sorsix.pocetna.exchange

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/exchange-rate")
class ExchangeController(private val service: ExchangeRateService) {

    @GetMapping
    fun getRates(): ExchangeRateList = service.getRates()
}
