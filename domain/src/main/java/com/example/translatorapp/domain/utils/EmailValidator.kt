package com.example.translatorapp.domain.utils

object EmailValidator {
    private val EMAIL_REGEX =
        Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    fun isValid(email: String): Boolean =
        EMAIL_REGEX.matches(email)
}