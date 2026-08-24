package com.example.translatorapp.domain.usecase.dataStore

import com.example.translatorapp.domain.repository.GlobalRepository

class SwapLanguagesUseCase(private val globalRepository: GlobalRepository) {
    suspend operator fun invoke(sourceCode: String, destinationCode: String) {
        globalRepository.setLanguages(
            sourceCode = sourceCode,
            destinationCode = destinationCode,
        )
    }
}