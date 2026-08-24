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
    val relatedSearches: List<String>,
    val googleFallbackUrl: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SerperResponse(
    val organic: List<SerperOrganic> = emptyList(),
    val relatedSearches: List<SerperRelated> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SerperOrganic(
    val title: String = "",
    val link: String = "",
    val snippet: String = "",
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SerperRelated(
    val query: String = "",
)
