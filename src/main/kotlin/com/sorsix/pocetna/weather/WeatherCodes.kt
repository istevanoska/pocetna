package com.sorsix.pocetna.weather

/** Macedonian labels for the WMO weather codes returned by Open-Meteo. */
object WeatherCodes {
    private val descriptions = mapOf(
        0 to "Ведро",
        1 to "Претежно ведро",
        2 to "Делумно облачно",
        3 to "Облачно",
        45 to "Магла",
        48 to "Слана магла",
        51 to "Слаб ситен дожд",
        53 to "Ситен дожд",
        55 to "Густ ситен дожд",
        56 to "Мразовит ситен дожд",
        57 to "Густ мразовит ситен дожд",
        61 to "Слаб дожд",
        63 to "Дожд",
        65 to "Силен дожд",
        66 to "Мразовит дожд",
        67 to "Силен мразовит дожд",
        71 to "Слаб снег",
        73 to "Снег",
        75 to "Силен снег",
        77 to "Снежни зрна",
        80 to "Слаби прелики дождови",
        81 to "Прелики дождови",
        82 to "Силни прелики дождови",
        85 to "Слаби снежни прелики",
        86 to "Силни снежни прелики",
        95 to "Грмежи со дожд",
        96 to "Грмежи со град",
        99 to "Силни грмежи со град",
    )

    fun describe(code: Int): String = descriptions[code] ?: "Непознато"
}
