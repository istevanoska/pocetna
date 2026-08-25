package com.sorsix.pocetna.account

import org.springframework.data.jpa.repository.JpaRepository

interface SubscriberRepository : JpaRepository<Subscriber, Long> {
    fun findByEmail(email: String): Subscriber?
}
