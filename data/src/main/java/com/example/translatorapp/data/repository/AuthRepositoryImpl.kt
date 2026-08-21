package com.example.translatorapp.data.repository

import android.util.Log
import com.example.translatorapp.data.mapper.entity.toDomainUser
import com.example.translatorapp.data.mapper.error.FirebaseAuthErrorMapper
import com.example.translatorapp.domain.error.AuthError
import com.example.translatorapp.domain.model.base.User
import com.example.translatorapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val errorMapper: FirebaseAuthErrorMapper,
) : AuthRepository {
    override fun getCurrentUser(): User? = firebaseAuth.currentUser?.toDomainUser()

    override fun observeAuthState(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toDomainUser())
        }

        firebaseAuth.addAuthStateListener(listener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }.onStart {
        emit(getCurrentUser())

        Log.d("AUTH_DEBUG", "On app start: ${getCurrentUser()}")
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(AuthError.EmptyCredentials())
        }

        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()

            val user = firebaseAuth.currentUser?.toDomainUser()
            if (user != null) {
                Result.success(Unit)
            } else {
                Result.failure(AuthError.Unknown("Login: Cannot get a user from Firebase!"))
            }

        } catch (exc: Exception) {
            if (exc is CancellationException) throw exc

            Result.failure(errorMapper(exc))
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        return try {
            val credentials = GoogleAuthProvider.getCredential(idToken, null)

            firebaseAuth.signInWithCredential(credentials).await()

            val user = firebaseAuth.currentUser?.toDomainUser()
            if (user != null) {
                Result.success(Unit)
            } else {
                Result.failure(
                    AuthError.Unknown("Login: Cannot get a user from Firebase!")
                )
            }

        } catch (exc: Exception) {
            if (exc is CancellationException) throw exc

            Result.failure(errorMapper(exc))
        }
    }

    override suspend fun register(email: String, password: String): Result<Unit> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(AuthError.EmptyCredentials())
        }

        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            val user = firebaseAuth.currentUser?.toDomainUser()
            if (user != null) {
                Result.success(Unit)
            } else {
                Result.failure(
                    AuthError.Unknown("Registration: Cannot get a user from Firebase!")
                )
            }
        } catch (exc: Exception) {
            if (exc is CancellationException) throw exc

            Result.failure(errorMapper(exc))
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        if (email.isBlank()) {
            return Result.failure(AuthError.EmptyCredentials())
        }

        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (exc: Exception) {
            if (exc is CancellationException) throw exc

            Result.failure(errorMapper(exc))
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}