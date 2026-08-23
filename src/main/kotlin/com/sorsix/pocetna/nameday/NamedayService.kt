package com.sorsix.pocetna.nameday

import org.springframework.stereotype.Service
import java.time.LocalDate

data class TodayInfo(
    val isoDate: String,
    val displayDate: String,
    val dayOfWeek: String,
    val names: List<String>,
)

/**
 * A curated subset of well-known Macedonian Orthodox name days (imendar), keyed by
 * MM-dd on the civil (Gregorian) calendar. This intentionally is not exhaustive -
 * only dates we're confident are correct are included, so the widget never guesses.
 */
@Service
class NamedayService {

    private val dayNames = listOf("Недела", "Понеделник", "Вторник", "Среда", "Четврток", "Петок", "Сабота")
    private val monthNames = listOf(
        "јануари", "февруари", "март", "април", "мај", "јуни",
        "јули", "август", "септември", "октомври", "ноември", "декември",
    )

    private val namedays: Map<String, List<String>> = mapOf(
        "01-02" to listOf("Игнат"),
        "01-09" to listOf("Стефан", "Стефанија"),
        "01-14" to listOf("Васил", "Василка"),
        "01-19" to listOf("Јован", "Јордан", "Јорданка"),
        "01-20" to listOf("Јован"),
        "02-14" to listOf("Трифун"),
        "03-22" to listOf("Младен", "Младенка"),
        "05-06" to listOf("Ѓорѓи", "Ѓорѓина"),
        "05-24" to listOf("Кирил", "Методија"),
        "07-07" to listOf("Иван"),
        "08-02" to listOf("Илија", "Иле"),
        "08-28" to listOf("Марија"),
        "09-11" to listOf("Јован"),
        "09-21" to listOf("Марија"),
        "09-27" to listOf("Крсте", "Крстина"),
        "10-27" to listOf("Петар", "Петра"),
        "11-08" to listOf("Димитар", "Митре", "Митра"),
        "12-13" to listOf("Андреј", "Андреа"),
        "12-19" to listOf("Никола", "Николина"),
    )

    fun today(): TodayInfo {
        val date = LocalDate.now()
        val key = "%02d-%02d".format(date.monthValue, date.dayOfMonth)
        return TodayInfo(
            isoDate = date.toString(),
            displayDate = "${date.dayOfMonth} ${monthNames[date.monthValue - 1]} ${date.year}",
            dayOfWeek = dayNames[date.dayOfWeek.value % 7],
            names = namedays[key] ?: emptyList(),
        )
    }
}
