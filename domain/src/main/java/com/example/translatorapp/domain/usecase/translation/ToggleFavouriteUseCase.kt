package com.example.translatorapp.domain.usecase.translation

import com.example.translatorapp.domain.repository.TranslationRepository

class ToggleFavouriteUseCase(private val translationRepository: TranslationRepository) {
    suspend operator fun invoke(id: String, isFavourite: Boolean): Result<Unit> =
        translationRepository.toggleFavourite(id = id, isFavourite = isFavourite)
}