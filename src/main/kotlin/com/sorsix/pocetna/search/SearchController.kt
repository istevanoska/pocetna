package com.sorsix.pocetna.search

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/search")
class SearchController(private val service: GoogleSearchService) {

    @GetMapping
    fun search(@RequestParam q: String): SearchResponse = service.search(q)
}
