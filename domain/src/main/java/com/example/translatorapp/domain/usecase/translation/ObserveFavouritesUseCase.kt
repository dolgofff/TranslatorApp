package com.example.translatorapp.domain.usecase.translation

import com.example.translatorapp.domain.model.Translation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveFavouritesUseCase(
    private val translationsFlow: ObserveTranslationsUseCase,
) {
    operator fun invoke(): Flow<Result<List<Translation>>> =
        translationsFlow()
            .map { result ->
                result.map { translationsList ->
                    translationsList.filter { it.isFavourite }
                }
            }
}
