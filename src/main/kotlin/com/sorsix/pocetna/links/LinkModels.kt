package com.sorsix.pocetna.links

data class SiteLink(
    val name: String,
    val url: String,
    val description: String = "",
)

data class LinkCategory(
    val id: String,
    val title: String,
    val icon: String,
    val links: List<SiteLink>,
)
