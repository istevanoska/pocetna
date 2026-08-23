package com.sorsix.pocetna.search

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class SearchResult(
    val title: String,
    val link: String,
    val snippet: String,
    val displayLink: String,
)

data class SearchResponse(
    val query: String,
    val configured: Boolean,
    val results: List<SearchResult>,
    val googleFallbackUrl: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GoogleCseResponse(
    val items: List<GoogleCseItem> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GoogleCseItem(
    val title: String = "",
    val link: String = "",
    val snippet: String = "",
    val displayLink: String = "",
)
