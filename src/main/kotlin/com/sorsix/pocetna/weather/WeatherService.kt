package com.sorsix.pocetna.weather

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.sorsix.pocetna.common.TtlCacheMap
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.time.Duration

val MACEDONIAN_CITIES = listOf(
    WeatherCity("skopje", "Скопје", 41.9981, 21.4254),
    WeatherCity("bitola", "Битола", 41.0297, 21.3347),
    WeatherCity("kumanovo", "Куманово", 42.1322, 21.7144),
    WeatherCity("ohrid", "Охрид", 41.1231, 20.8016),
    WeatherCity("prilep", "Прилеп", 41.3453, 21.5541),
    WeatherCity("tetovo", "Тетово", 42.0100, 20.9714),
    WeatherCity("strumica", "Струмица", 41.4378, 22.6431),
    WeatherCity("veles", "Велес", 41.7156, 21.7756),
    WeatherCity("shtip", "Штип", 41.7458, 22.1953),
    WeatherCity("gostivar", "Гостивар", 41.7967, 20.9114),
    WeatherCity("kavadarci", "Кавадарци", 41.4331, 22.0114),
    WeatherCity("struga", "Струга", 41.1775, 20.6764),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpenMeteoResponse(
    val current: CurrentBlock?,
    val daily: DailyBlock?,
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class CurrentBlock(
        val temperature_2m: Double = 0.0,
        val relative_humidity_2m: Int = 0,
        val wind_speed_10m: Double = 0.0,
        val weather_code: Int = 0,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class DailyBlock(
        val time: List<String> = emptyList(),
        val temperature_2m_max: List<Double> = emptyList(),
        val temperature_2m_min: List<Double> = emptyList(),
        val weather_code: List<Int> = emptyList(),
    )
}

@Service
class WeatherService(private val restClient: RestClient) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val cache = TtlCacheMap<String, CurrentWeather>(ttl = Duration.ofMinutes(30)) { cityId -> fetchOrThrow(cityId) }

    fun cities(): List<WeatherCity> = MACEDONIAN_CITIES

    fun getWeather(cityId: String): CurrentWeather? {
        if (MACEDONIAN_CITIES.none { it.id == cityId }) return null
        return try {
            cache.get(cityId)
        } catch (ex: Exception) {
            log.warn("Failed to fetch weather for {}", cityId, ex)
            null
        }
    }

    private fun fetchOrThrow(cityId: String): CurrentWeather {
        val city = MACEDONIAN_CITIES.first { it.id == cityId }
        val response = restClient.get()
            .uri(
                "https://api.open-meteo.com/v1/forecast" +
                    "?latitude={lat}&longitude={lon}" +
                    "&current=temperature_2m,weather_code,wind_speed_10m,relative_humidity_2m" +
                    "&daily=temperature_2m_max,temperature_2m_min,weather_code" +
                    "&forecast_days=5&timezone=Europe/Skopje",
                city.lat, city.lon,
            )
            .retrieve()
            .body<OpenMeteoResponse>()
        val current = checkNotNull(response?.current) { "Open-Meteo returned no current weather" }
        val daily = response.daily
        val forecast = daily?.time?.indices?.map { i ->
            DailyForecast(
                date = daily.time[i],
                maxTemp = daily.temperature_2m_max.getOrElse(i) { 0.0 },
                minTemp = daily.temperature_2m_min.getOrElse(i) { 0.0 },
                weatherCode = daily.weather_code.getOrElse(i) { 0 },
                description = WeatherCodes.describe(daily.weather_code.getOrElse(i) { 0 }),
            )
        } ?: emptyList()

        return CurrentWeather(
            city = city.name,
            temperature = current.temperature_2m,
            humidity = current.relative_humidity_2m,
            windSpeed = current.wind_speed_10m,
            weatherCode = current.weather_code,
            description = WeatherCodes.describe(current.weather_code),
            forecast = forecast,
        )
    }
}
