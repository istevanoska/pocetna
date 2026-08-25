package com.sorsix.pocetna.account

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

data class RegisterRequest(val email: String?, val name: String?)
data class RegisterResponse(val email: String, val name: String, val alreadyRegistered: Boolean)

@RestController
@RequestMapping("/api/register")
class AccountController(private val repository: SubscriberRepository) {

    @PostMapping
    fun register(@RequestBody req: RegisterRequest): RegisterResponse {
        val email = req.email?.trim()?.lowercase().orEmpty()
        if (email.isBlank() || !email.contains("@")) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Невалидна е-пошта")
        }
        val name = req.name?.trim().takeUnless { it.isNullOrBlank() } ?: email.substringBefore("@")

        val existing = repository.findByEmail(email)
        val saved = if (existing != null) {
            existing.name = name
            repository.save(existing)
        } else {
            repository.save(Subscriber(email = email, name = name))
        }
        return RegisterResponse(saved.email, saved.name, alreadyRegistered = existing != null)
    }

    /** Simple count so you can confirm registrations are being stored. */
    @GetMapping("/count")
    fun count(): Map<String, Long> = mapOf("count" to repository.count())
}
