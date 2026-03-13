package com.example.translatorapp.domain.usecase.authorization

import com.example.translatorapp.domain.repository.AuthRepository

class SignInEmailUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) =
        authRepository.signInWithEmail(email = email, password = password)
}