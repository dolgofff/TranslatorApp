package com.example.translatorapp.domain.error

interface TranslationErrorMapper {
    operator fun invoke(exc: Throwable): TranslationError
}