package com.sorsix.pocetna.config

import com.sorsix.pocetna.links.LinkDirectoryService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.nio.charset.StandardCharsets

/**
 * robots.txt and sitemap.xml.
 *
 * Both are served from Spring rather than kept as static files so the sitemap is
 * generated from LinkDirectoryService — the same single source of truth the pages
 * themselves come from. A category page therefore appears in the sitemap the moment
 * it gets hasPage = true, and the sitemap can never list a URL that 404s.
 *
 * Paths containing a dot are deliberately not matched by SpaForwardController, so
 * these two mappings are the only handlers for them.
 */
@Controller
class SeoController(
    private val links: LinkDirectoryService,
    @Value("\${app.site.base-url}") private val baseUrl: String,
) {

    private val plain = MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)
    private val xml = MediaType(MediaType.APPLICATION_XML, StandardCharsets.UTF_8)

    /** Everything crawlable except the API: search engines and AI crawlers alike. */
    @GetMapping("/robots.txt")
    @ResponseBody
    fun robots(): ResponseEntity<String> {
        val body = """
            User-agent: *
            Allow: /
            Disallow: /api/

            Sitemap: ${site()}/sitemap.xml
        """.trimIndent() + "\n"

        return ResponseEntity.ok().contentType(plain).body(body)
    }

    /** The homepage plus every category that has its own prerendered page. */
    @GetMapping("/sitemap.xml")
    @ResponseBody
    fun sitemap(): ResponseEntity<String> {
        val urls = buildList {
            add("${site()}/")
            links.getCategories().filter { it.hasPage }.forEach { add("${site()}/${it.id}") }
        }

        val body = buildString {
            append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n")
            urls.forEach { append("  <url><loc>$it</loc></url>\n") }
            append("</urlset>\n")
        }

        return ResponseEntity.ok().contentType(xml).body(body)
    }

    /** No trailing slash, so callers can append their own path. */
    private fun site(): String = baseUrl.trimEnd('/')
}
