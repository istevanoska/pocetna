package com.sorsix.pocetna.links

import tools.jackson.databind.SerializationFeature
import tools.jackson.databind.json.JsonMapper
import java.io.File

/**
 * Build-time export of the link directory.
 *
 * The Angular build prerenders the site to static HTML so that crawlers which do not
 * run JavaScript still see the links. Prerendering happens before any backend is
 * running, so the renderer cannot call /api/links. This writes exactly what that
 * endpoint serves into a JSON file the prerenderer reads instead.
 *
 * The Kotlin list in LinkDirectoryService stays the single source of truth — this
 * only serialises it. Run with `./gradlew exportLinks`; the Docker build runs it
 * before `npm run build`, so the deployed snapshot can never drift from the list.
 */
fun main(args: Array<String>) {
    val target = File(args.firstOrNull() ?: "frontend/src/links.snapshot.json")
    val service = LinkDirectoryService()

    val categories = service.getCategories()
    val pages = categories
        .filter { it.hasPage }
        .mapNotNull { service.getPage(it.id) }
        .associateBy { it.id }

    val snapshot = mapOf(
        "categories" to categories,
        "pages" to pages,
    )

    target.parentFile?.mkdirs()
    // Jackson 3 mappers are immutable and built, not configured after construction.
    // Indented so the committed snapshot stays reviewable in a diff.
    val mapper = JsonMapper.builder()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .build()

    target.writeText(mapper.writeValueAsString(snapshot))

    println("exportLinks: ${categories.size} categories, ${pages.size} pages -> ${target.path}")
}
