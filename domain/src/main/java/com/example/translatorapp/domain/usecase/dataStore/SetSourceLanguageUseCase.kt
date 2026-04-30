package com.example.translatorapp.domain.usecase.dataStore

import com.example.translatorapp.domain.repository.GlobalRepository

class SetSourceLanguageUseCase(private val globalRepository: GlobalRepository) {
    suspend operator fun invoke(code: String) {
        globalRepository.setSourceLanguage(code)
    }
}