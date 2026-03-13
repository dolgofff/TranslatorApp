package com.example.translatorapp.domain.usecase.authorization

import com.example.translatorapp.domain.error.AuthError
import com.example.translatorapp.domain.repository.AuthRepository
import com.example.translatorapp.domain.utils.EmailValidator

class ResetPasswordUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (!EmailValidator.isValid(email))
            return Result.failure(AuthError.InvalidEmail())

        return authRepository.resetPassword(email)
    }
}