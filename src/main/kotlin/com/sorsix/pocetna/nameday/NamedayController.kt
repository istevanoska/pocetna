package com.sorsix.pocetna.nameday

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/nameday")
class NamedayController(private val service: NamedayService) {

    @GetMapping
    fun today(): TodayInfo = service.today()
}
