package com.example.translatorapp.domain.usecase.authorization

import com.example.translatorapp.domain.repository.AuthRepository

class AuthStateUseCase(private val authRepository: AuthRepository) {
    operator fun invoke() = authRepository.observeAuthState()
}