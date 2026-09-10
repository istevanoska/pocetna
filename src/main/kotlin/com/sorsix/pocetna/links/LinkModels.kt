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
    /** True when the category has its own page of subcategories at /<id>. */
    val hasPage: Boolean = false,
)

/** One named group of links on a category page. */
data class CategorySection(
    val title: String,
    val links: List<SiteLink>,
)

data class CategoryPage(
    val id: String,
    val title: String,
    val icon: String,
    val sections: List<CategorySection>,
)
