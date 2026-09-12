package com.sorsix.pocetna.radio

import org.springframework.stereotype.Service

/**
 * Live radio streams, hardcoded like the link directory.
 *
 * Only https streams belong here: the site is served over https, so a plain-http
 * stream is mixed content and every browser blocks it silently.
 *
 * The streams below are the ones Канал 77 publishes on its own player at
 * https://radio.kanal77.mk/ . The widget credits and links back to them.
 */
@Service
class RadioService {

    private val stations: List<RadioStation> = listOf(
        station("kanal77", "Канал 77", "Хитови и вести"),
        station("makedonsko", "Канал 77 Македонско", "Македонска музика"),
        station("exyunew", "Канал 77 Ex-YU", "Ex-YU хитови"),
        station("cafe", "Канал 77 Café", "Опуштена музика"),
        station("freshnew", "Канал 77 Fresh", "Најнови хитови"),
        station("rock", "Канал 77 Rock", "Рок"),
        station("deephouse", "Канал 77 Deep House", "Deep house"),
        station("millennium", "Канал 77 Millennium", "Хитови од 2000-тите"),
        station("folk", "Канал 77 Folk", "Народна музика"),
        station("folkmk", "Канал 77 Folk МК", "Македонска народна музика"),
        station("80s", "Канал 77 80's", "Осумдесетти"),
        station("90s", "Канал 77 90's", "Деведесетти"),
        station("dance", "Канал 77 Dance", "Денс"),
    )

    fun getStations(): List<RadioStation> = stations

    private fun station(slug: String, name: String, description: String) = RadioStation(
        id = slug,
        name = name,
        description = description,
        streamUrl = "https://radiocnd.mms.mk/proxy/$slug/stream",
        homepage = "https://radio.kanal77.mk/",
    )
}
