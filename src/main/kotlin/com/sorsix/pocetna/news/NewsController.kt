package com.sorsix.pocetna.news

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/news")
class NewsController(private val service: NewsService) {

    @GetMapping
    fun getNews(): List<NewsItem> = service.getNews()
}
