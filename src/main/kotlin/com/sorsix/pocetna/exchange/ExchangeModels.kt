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

/**
 * One row as NBRM's documented web service returns it. Property names match the JSON
 * exactly; every field is optional so a new field on their side cannot break parsing.
 * Documented at https://www.nbrm.mk/web-servis-novo.nspx
 */
data class NbrmRate(
    val oznaka: String? = null,
    val nazivMak: String? = null,
    val nomin: Int = 1,
    val sreden: Double = 0.0,
    val datum: String? = null,
)
