package com.sorsix.pocetna.links

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/links")
class LinkController(private val service: LinkDirectoryService) {

    @GetMapping
    fun getLinks(): List<LinkCategory> = service.getCategories()
}
