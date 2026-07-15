package com.example.translatorapp.domain.repository

import com.example.translatorapp.domain.model.base.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signInWithEmail(email: String, password: String): Result<Unit>

    suspend fun signInWithGoogle(idToken: String): Result<Unit>

    suspend fun register(email: String, password: String): Result<Unit>

    suspend fun resetPassword(email: String): Result<Unit>

    fun logout()

    fun observeAuthState(): Flow<User?>
    fun getCurrentUser(): User?
}