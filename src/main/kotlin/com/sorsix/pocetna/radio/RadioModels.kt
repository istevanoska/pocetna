package com.sorsix.pocetna.radio

data class RadioStation(
    val id: String,
    val name: String,
    val description: String,
    /** Direct audio stream. Must be https, or browsers block it as mixed content. */
    val streamUrl: String,
    /** The broadcaster's own page, so the widget can credit and link back to them. */
    val homepage: String,
)
