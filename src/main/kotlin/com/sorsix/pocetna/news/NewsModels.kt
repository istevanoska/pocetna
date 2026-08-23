package com.sorsix.pocetna.news

data class NewsItem(
    val title: String,
    val link: String,
    val pubDate: String,
    val category: String?,
    val source: String,
)
