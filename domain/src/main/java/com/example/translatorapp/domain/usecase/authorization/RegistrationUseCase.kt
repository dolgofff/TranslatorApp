package com.example.translatorapp.domain.usecase.authorization

import com.example.translatorapp.domain.error.AuthError
import com.example.translatorapp.domain.model.User
import com.example.translatorapp.domain.repository.AuthRepository

class RegistrationUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String,
    ): Result<Unit> {
        if (password != confirmPassword)
            return Result.failure(AuthError.Unknown("Passwords do not match"))

        return authRepository.register(email = email, password = password)
    }
}