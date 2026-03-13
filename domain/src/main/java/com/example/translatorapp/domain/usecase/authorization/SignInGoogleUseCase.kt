package com.example.translatorapp.domain.usecase.authorization

import com.example.translatorapp.domain.repository.AuthRepository

class SignInGoogleUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(idToken: String) =
        authRepository.signInWithGoogle(idToken = idToken)
}