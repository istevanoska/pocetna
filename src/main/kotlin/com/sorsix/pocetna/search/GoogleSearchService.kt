package com.sorsix.pocetna.search

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import tools.jackson.databind.ObjectMapper
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Fetches real Google results via Serper.dev (a Google Search API proxy) and
 * returns them for in-page display. The API key stays server-side.
 */
@Service
class GoogleSearchService(
    private val restClient: RestClient,
    private val objectMapper: ObjectMapper,
    @Value("\${serper.api-key:}") private val apiKey: String,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val isConfigured: Boolean
        get() = apiKey.isNotBlank()

    fun search(query: String): SearchResponse {
        val fallbackUrl = "https://www.google.com/search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8)

        if (!isConfigured || query.isBlank()) {
            return SearchResponse(query, isConfigured, emptyList(), emptyList(), fallbackUrl)
        }

        return try {
            val response = restClient.post()
                .uri("https://google.serper.dev/search")
                .header("X-API-KEY", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapOf("q" to query, "gl" to "mk", "hl" to "mk", "num" to 10))
                .retrieve()
                .body<SerperResponse>()

            val results = response?.organic?.map {
                SearchResult(
                    title = it.title,
                    link = it.link,
                    snippet = it.snippet,
                    displayLink = displayHost(it.link),
                )
            } ?: emptyList()

            val related = suggestions(query)

            SearchResponse(query, true, results, related, fallbackUrl)
        } catch (ex: Exception) {
            // On failure report as not-configured so the frontend opens Google instead of an empty panel.
            log.warn("Serper search request failed, falling back to Google", ex)
            SearchResponse(query, false, emptyList(), emptyList(), fallbackUrl)
        }
    }

    private fun displayHost(link: String): String =
        try {
            URI(link).host?.removePrefix("www.") ?: link
        } catch (_: Exception) {
            link
        }

    /** Free Google autocomplete suggestions (no API key) used as "related searches". */
    private fun suggestions(query: String): List<String> =
        try {
            val encoded = URLEncoder.encode(query, StandardCharsets.UTF_8)
            val raw = restClient.get()
                .uri("https://suggestqueries.google.com/complete/search?client=firefox&hl=mk&q=$encoded")
                .retrieve()
                .body(String::class.java)
            val node = objectMapper.readTree(raw ?: "[]")
            node.get(1)?.mapNotNull { it.asString() }?.take(8) ?: emptyList()
        } catch (ex: Exception) {
            log.warn("Suggestions request failed", ex)
            emptyList()
        }
}
