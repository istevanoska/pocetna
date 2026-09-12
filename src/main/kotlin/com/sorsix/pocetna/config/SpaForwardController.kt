package com.sorsix.pocetna.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

/**
 * Paths such as /obrazovanie are Angular routes, not files. In production the built
 * frontend is served by Spring out of static/, so opening or refreshing such a URL
 * would otherwise return 404.
 *
 * The production build prerenders each route to its own static file, so a route that
 * was prerendered has a matching "<path>/index.html" in static/ — serve that, so the
 * visitor and any crawler get the real page rather than an empty shell. Anything else
 * falls back to the root index.html and lets the Angular router resolve it in the
 * browser, which is what happens for a URL that matches no prerendered page.
 *
 * In development this never fires: the dev server on 4200 serves the app and only
 * proxies API calls here.
 */
@Controller
class SpaForwardController {

    @GetMapping("/{path:^(?!api$)[^.]+}", "/{path:^(?!api$)[^.]+}/{sub:[^.]+}")
    fun forward(request: HttpServletRequest): String {
        val path = request.requestURI.trim('/')
        if (path.isNotEmpty() && ClassPathResource("static/$path/index.html").exists()) {
            return "forward:/$path/index.html"
        }
        return "forward:/index.html"
    }
}
