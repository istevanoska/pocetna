package com.sorsix.pocetna.search

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Service
class GoogleSearchService(
    private val restClient: RestClient,
    @Value("\${google.search.api-key}") private val apiKey: String,
    @Value("\${google.search.cse-id}") private val cseId: String,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val isConfigured: Boolean
        get() = apiKey.isNotBlank() && cseId.isNotBlank()

    fun search(query: String): SearchResponse {
        val fallbackUrl = "https://www.google.com/search?q=" + java.net.URLEncoder.encode(query, Charsets.UTF_8)

        if (!isConfigured || query.isBlank()) {
            return SearchResponse(query = query, configured = isConfigured, results = emptyList(), googleFallbackUrl = fallbackUrl)
        }

        return try {
            val response = restClient.get()
                .uri(
                    "https://www.googleapis.com/customsearch/v1?key={key}&cx={cx}&q={q}&num=10",
                    apiKey, cseId, query,
                )
                .retrieve()
                .body<GoogleCseResponse>()

            val results = response?.items?.map {
                SearchResult(title = it.title, link = it.link, snippet = it.snippet, displayLink = it.displayLink)
            } ?: emptyList()

            SearchResponse(query = query, configured = true, results = results, googleFallbackUrl = fallbackUrl)
        } catch (ex: Exception) {
            // On any API failure (e.g. Custom Search API not enabled yet) report as
            // not-configured so the frontend gracefully opens Google instead of an empty panel.
            log.warn("Google Custom Search request failed, falling back to Google", ex)
            SearchResponse(query = query, configured = false, results = emptyList(), googleFallbackUrl = fallbackUrl)
        }
    }
}
