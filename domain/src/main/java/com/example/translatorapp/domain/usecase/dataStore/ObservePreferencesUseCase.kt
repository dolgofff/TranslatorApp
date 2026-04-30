package com.example.translatorapp.domain.usecase.dataStore

import com.example.translatorapp.domain.repository.GlobalRepository

class ObservePreferencesUseCase(private val globalRepository: GlobalRepository) {
    operator fun invoke() = globalRepository.preferencesFlow
}