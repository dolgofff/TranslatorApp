package com.example.translatorapp.domain.usecase.authorization

import com.example.translatorapp.domain.repository.AuthRepository

class GetCurrentUserUseCase(private val authRepository: AuthRepository) {
    operator fun invoke() = authRepository.getCurrentUser()
}