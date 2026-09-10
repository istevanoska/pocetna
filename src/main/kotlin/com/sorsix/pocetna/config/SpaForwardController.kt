package com.sorsix.pocetna.config

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

/**
 * Paths such as /obrazovanie are Angular routes, not files. In production the built
 * frontend is served by Spring out of static/, so opening or refreshing such a URL
 * would otherwise return 404. Forward anything that is not an API path and carries
 * no file extension to index.html, and let the Angular router resolve it.
 *
 * In development this never fires: the dev server on :4200 serves the app and only
 * proxies API calls here.
 */
@Controller
class SpaForwardController {

    @GetMapping("/{path:^(?!api$)[^.]+}", "/{path:^(?!api$)[^.]+}/{sub:[^.]+}")
    fun forward(): String = "forward:/index.html"
}
