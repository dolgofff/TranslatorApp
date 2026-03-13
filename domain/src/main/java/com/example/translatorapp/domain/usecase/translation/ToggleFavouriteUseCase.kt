package com.example.translatorapp.domain.usecase.translation

import com.example.translatorapp.domain.error.TranslationErrorMapper
import com.example.translatorapp.domain.repository.TranslationRepository

class ToggleFavouriteUseCase(
    private val translationRepository: TranslationRepository,
    private val translationErrorMapper: TranslationErrorMapper,
) {
    suspend operator fun invoke(id: String, isFavourite: Boolean) =
        try {
            translationRepository.toggleFavourite(id = id, isFavourite = isFavourite)
            Result.success(Unit)
        } catch (exc: Throwable) {
            Result.failure(translationErrorMapper(exc))
        }
}