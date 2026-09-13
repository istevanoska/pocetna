package com.sorsix.pocetna.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.nio.charset.StandardCharsets

/**
 * Paths such as /obrazovanie are Angular routes, not files. In production the built
 * frontend is served by Spring out of static/, so opening or refreshing such a URL
 * would otherwise return 404.
 *
 * The production build prerenders every route to its own static file, so a route that
 * exists has a matching "<path>/index.html" in static/ — serve that, and the visitor
 * and any crawler get the real page. A path with no such file is a URL that does not
 * exist, and it now says so with a real 404 instead of quietly serving the homepage:
 * once the homepage is prerendered, answering every invented URL with 200 and a full
 * copy of it turns the whole domain into indexable duplicate content.
 *
 * In development this never fires: the dev server on 4200 serves the app and only
 * proxies API calls here.
 */
@Controller
class SpaForwardController {

    private val html = MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8)

    @GetMapping("/{path:^(?!api$)[^.]+}", "/{path:^(?!api$)[^.]+}/{sub:[^.]+}")
    @ResponseBody
    fun forward(request: HttpServletRequest): ResponseEntity<Resource> {
        val path = request.requestURI.trim('/')

        val prerendered = ClassPathResource("static/$path/index.html")
        if (path.isNotEmpty() && prerendered.exists()) {
            return ResponseEntity.ok().contentType(html).body(prerendered)
        }

        val notFound = ClassPathResource("static/404.html")
        val response = ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(html)
        return if (notFound.exists()) response.body(notFound) else response.build()
    }
}
