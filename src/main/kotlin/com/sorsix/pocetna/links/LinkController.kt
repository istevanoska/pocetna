package com.sorsix.pocetna.links

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/links")
class LinkController(private val service: LinkDirectoryService) {

    @GetMapping
    fun getLinks(): List<LinkCategory> = service.getCategories()

    @GetMapping("/{id}")
    fun getPage(@PathVariable id: String): CategoryPage =
        service.getPage(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No page for category $id")
}
