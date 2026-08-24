package com.example.translatorapp.data.repository

import com.example.translatorapp.data.entity.TranslationEntity
import com.example.translatorapp.data.mapper.entity.toDomainTranslation
import com.example.translatorapp.data.mapper.entity.toEntityTranslation
import com.example.translatorapp.data.mapper.error.FirebaseTranslationErrorMapper
import com.example.translatorapp.domain.error.TranslationError
import com.example.translatorapp.domain.model.base.Translation
import com.example.translatorapp.domain.repository.TranslationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class TranslationRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val errorMapper: FirebaseTranslationErrorMapper,
) : TranslationRepository {
    private fun userTranslations() =
        firebaseAuth.currentUser?.uid?.let { uid ->
            fireStore
                .collection("users")
                .document(uid)
                .collection("translations")
        }

    override suspend fun saveTranslation(translation: Translation): Result<Unit> =
        try {
            val userTranslations = userTranslations()
                ?: return Result.failure(TranslationError.NotAuthenticated())

            val translationEntity = translation.toEntityTranslation()

            userTranslations
                .document(translationEntity.id)
                .set(translationEntity, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (exc: Exception) {
            if (exc is CancellationException) throw exc

            Result.failure(errorMapper(exc))
        }

    override suspend fun deleteTranslation(id: String): Result<Unit> =
        try {
            val userTranslations = userTranslations()
                ?: return Result.failure(TranslationError.NotAuthenticated())

            userTranslations
                .document(id)
                .delete()
                .await()

            Result.success(Unit)
        } catch (exc: Exception) {
            if (exc is CancellationException) throw exc

            Result.failure(errorMapper(exc))
        }

    override suspend fun toggleFavourite(id: String, isFavourite: Boolean): Result<Unit> =
        try {
            val userTranslations = userTranslations()
                ?: return Result.failure(TranslationError.NotAuthenticated())

            userTranslations
                .document(id)
                .update("isFavourite", isFavourite)
                .await()

            Result.success(Unit)
        } catch (exc: Exception) {
            if (exc is CancellationException) throw exc

            Result.failure(errorMapper(exc))
        }

    override suspend fun clearHistory(): Result<Unit> =
        try {
            val userTranslations = userTranslations()
                ?: return Result.failure(TranslationError.NotAuthenticated())

            val data = userTranslations
                .whereEqualTo("isFavourite", false)
                .get()
                .await()

            val batch = fireStore.batch()

            data.documents.forEach { document ->
                batch.delete(document.reference)
            }
            batch.commit().await()

            Result.success(Unit)
        } catch (exc: Exception) {
            if (exc is CancellationException) throw exc

            Result.failure(errorMapper(exc))
        }

    override fun observeCurrentTranslations(): Flow<List<Translation>> =
        callbackFlow {
            val userTranslations = userTranslations() ?: run {
                close(TranslationError.NotAuthenticated())

                return@callbackFlow
            }

            val source = userTranslations
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        close(errorMapper(error))

                        return@addSnapshotListener
                    }

                    val safeSnapshot = snapshot ?: run {
                        close(TranslationError.Unknown("Snapshot is null"))

                        return@addSnapshotListener
                    }

                    val translationsList = safeSnapshot.documents.mapNotNull { document ->
                        document.toObject(TranslationEntity::class.java)
                            ?.toDomainTranslation(document.id)
                    }

                    trySend(translationsList)
                }

            awaitClose { source.remove() }
        }
}