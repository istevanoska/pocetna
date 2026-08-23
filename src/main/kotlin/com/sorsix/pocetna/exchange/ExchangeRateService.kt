package com.sorsix.pocetna.exchange

import com.sorsix.pocetna.common.TtlCache
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.w3c.dom.Element
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.xml.parsers.DocumentBuilderFactory

/**
 * NBRM only exposes a plain-HTTP-POST "test" binding on its ASMX service (no JSON API),
 * so we POST form data and parse the returned XML by hand instead of pulling in a SOAP client.
 */
@Service
class ExchangeRateService(private val restClient: RestClient) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val cache = TtlCache(ttl = Duration.ofHours(1)) { fetchOrThrow() }

    fun getRates(): ExchangeRateList = try {
        cache.get()
    } catch (ex: Exception) {
        log.warn("Failed to fetch NBRM exchange rates", ex)
        emptyResult()
    }

    private fun fetchOrThrow(): ExchangeRateList {
        val today = LocalDate.now()
        val body = LinkedMultiValueMap<String, String>().apply {
            add("startDate", today.minusDays(7).format(dateFormat))
            add("endDate", today.format(dateFormat))
            add("isStateAuth", "false")
        }
        val xml = restClient.post()
            .uri("https://www.nbrm.mk/services/ExchangeRates.asmx/GetEXRates")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(body)
            .retrieve()
            .body<String>()
        check(!xml.isNullOrBlank()) { "NBRM returned an empty response" }
        return parse(xml)
    }

    private fun emptyResult() = ExchangeRateList(date = "", rates = emptyList())

    private fun parse(xml: String): ExchangeRateList {
        val factory = DocumentBuilderFactory.newInstance()
        val doc = factory.newDocumentBuilder().parse(xml.byteInputStream(Charsets.UTF_8))
        val dayNodes = doc.getElementsByTagName("ExchangeRatesByDay")

        // Days are returned oldest-first; walk backwards to find the most recent day
        // that actually has published rates (today's list may not exist yet).
        for (i in dayNodes.length - 1 downTo 0) {
            val dayElement = dayNodes.item(i) as? Element ?: continue
            val rateNodes = dayElement.getElementsByTagName("ExchangeRateStateAuthoritiesModel")
            if (rateNodes.length == 0) continue

            val date = dayElement.getElementsByTagName("Date").item(0)?.textContent?.take(10) ?: ""
            val rates = (0 until rateNodes.length).mapNotNull { j ->
                val el = rateNodes.item(j) as? Element ?: return@mapNotNull null
                val code = el.getElementsByTagName("Oznaka").item(0)?.textContent ?: return@mapNotNull null
                val name = el.getElementsByTagName("Naziv").item(0)?.textContent ?: code
                val nominal = el.getElementsByTagName("Nomin").item(0)?.textContent?.toIntOrNull() ?: 1
                val middle = el.getElementsByTagName("Sreden").item(0)?.textContent?.toDoubleOrNull()
                    ?: return@mapNotNull null
                ExchangeRate(code, name, nominal, middle)
            }
            if (rates.isNotEmpty()) return ExchangeRateList(date, rates)
        }
        error("NBRM response contained no published rates in the last 7 days")
    }
}
