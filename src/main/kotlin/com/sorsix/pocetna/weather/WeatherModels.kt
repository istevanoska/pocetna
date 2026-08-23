package com.sorsix.pocetna.weather

data class WeatherCity(val id: String, val name: String, val lat: Double, val lon: Double)

data class DailyForecast(
    val date: String,
    val maxTemp: Double,
    val minTemp: Double,
    val weatherCode: Int,
    val description: String,
)

data class CurrentWeather(
    val city: String,
    val temperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val weatherCode: Int,
    val description: String,
    val forecast: List<DailyForecast>,
)
