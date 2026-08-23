package com.sorsix.pocetna.exchange

data class ExchangeRate(
    val code: String,
    val name: String,
    val nominal: Int,
    val middleRate: Double,
)

data class ExchangeRateList(
    val date: String,
    val rates: List<ExchangeRate>,
)
