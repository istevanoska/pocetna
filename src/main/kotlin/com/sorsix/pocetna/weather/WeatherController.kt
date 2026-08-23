package com.sorsix.pocetna.weather

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/weather")
class WeatherController(private val service: WeatherService) {

    @GetMapping("/cities")
    fun getCities(): List<WeatherCity> = service.cities()

    @GetMapping
    fun getWeather(@RequestParam(defaultValue = "skopje") city: String): ResponseEntity<CurrentWeather> {
        val weather = service.getWeather(city) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(weather)
    }
}
