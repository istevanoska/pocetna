package com.sorsix.pocetna.exchange

import com.sorsix.pocetna.common.TtlCache
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Reads the курсна листа from the National Bank's documented web service:
 * GET /KLServiceNOV/GetExchangeRate?StartDate=dd.MM.yyyy and EndDate, format=json
 *
 * GetExchangeRate is the public exchange rate list. The sibling method GetExchangeRates
 * returns the rates used by state bodies for foreign payments, which is a different list.
 *
 * Rates are published once per working day, so the cache holds them for hours rather
 * than re-asking on every visit.
 */
@Service
class ExchangeRateService(private val restClient: RestClient) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val zone = ZoneId.of("Europe/Skopje")
    private val cache = TtlCache(
        ttl = Duration.ofHours(3),
        retryDelay = Duration.ofMinutes(10),
    ) { fetchOrThrow() }

    fun getRates(): ExchangeRateList = try {
        cache.get()
    } catch (ex: Exception) {
        log.warn("Failed to fetch NBRM exchange rates", ex)
        emptyResult()
    }

    private fun fetchOrThrow(): ExchangeRateList {
        val today = LocalDate.now(zone)
        val rows = restClient.get()
            .uri(
                "https://www.nbrm.mk/KLServiceNOV/GetExchangeRate" +
                    "?StartDate={start}&EndDate={end}&format=json",
                today.minusDays(7).format(dateFormat),
                today.format(dateFormat),
            )
            .retrieve()
            .body<List<NbrmRate>>()

        check(!rows.isNullOrEmpty()) { "NBRM returned no exchange rates" }

        // The response covers several days, oldest first. Keep the most recent one
        // that has rates: today's list may not be published yet.
        val latest = rows.mapNotNull { it.datum }.maxOrNull()
            ?: error("NBRM rows carried no date")

        val rates = rows.asSequence()
            .filter { it.datum == latest }
            .mapNotNull { row ->
                val code = row.oznaka?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                ExchangeRate(
                    code = code,
                    name = row.nazivMak?.takeIf { it.isNotBlank() } ?: code,
                    nominal = row.nomin,
                    middleRate = row.sreden,
                )
            }
            .toList()

        check(rates.isNotEmpty()) { "NBRM published no usable rates for $latest" }
        return ExchangeRateList(date = latest.take(10), rates = rates)
    }

    private fun emptyResult() = ExchangeRateList(date = "", rates = emptyList())
}
