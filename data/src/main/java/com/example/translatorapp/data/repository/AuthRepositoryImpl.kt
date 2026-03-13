package com.example.translatorapp.data.repository

import com.example.translatorapp.data.mapper.FirebaseAuthErrorMapper
import com.example.translatorapp.data.mapper.toDomainUser
import com.example.translatorapp.domain.error.AuthError
import com.example.translatorapp.domain.model.User
import com.example.translatorapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AuthRepositoryImpl(
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
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val user = firebaseAuth.currentUser?.toDomainUser()

                    if (user != null && cont.isActive)
                        cont.resume(Result.success(Unit))
                    else if (cont.isActive)
                        cont.resume(Result.failure(AuthError.Unknown("Login: Cannot get a user from Firebase!")))
                }.addOnFailureListener { exc ->
                    if (cont.isActive)
                        cont.resume(Result.failure(errorMapper(exc)))
                }
        }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            val credentials = GoogleAuthProvider.getCredential(idToken, null)

            firebaseAuth.signInWithCredential(credentials)
                .addOnSuccessListener {
                    val user = firebaseAuth.currentUser?.toDomainUser()

                    if (user != null && cont.isActive)
                        cont.resume(Result.success(Unit))
                    else if (cont.isActive)
                        cont.resume(Result.failure(AuthError.Unknown("Login: Cannot get a user from Firebase!")))
                }
                .addOnFailureListener { exc ->
                    if (cont.isActive)
                        cont.resume(Result.failure(errorMapper(exc)))
                }
        }

    override suspend fun register(email: String, password: String): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val user = firebaseAuth.currentUser?.toDomainUser()

                    if (user != null && cont.isActive)
                        cont.resume(Result.success(Unit))
                    else if (cont.isActive)
                        cont.resume(Result.failure(AuthError.Unknown("Registration: Cannot get a user from Firebase!")))
                }
                .addOnFailureListener { exc ->
                    if (cont.isActive)
                        cont.resume(Result.failure(errorMapper(exc)))
                }
        }

    override suspend fun resetPassword(email: String): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    if (cont.isActive)
                        cont.resume(Result.success(Unit))
                }
                .addOnFailureListener { exc ->
                    if (cont.isActive)
                        cont.resume(Result.failure(errorMapper(exc)))
                }
        }

    override fun logout() {
        firebaseAuth.signOut()
    }
}