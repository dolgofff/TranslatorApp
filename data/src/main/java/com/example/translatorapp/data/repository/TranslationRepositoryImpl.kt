package com.example.translatorapp.data.repository

import com.example.translatorapp.data.entity.TranslationEntity
import com.example.translatorapp.data.mapper.toDomainTranslation
import com.example.translatorapp.data.mapper.toEntityTranslation
import com.example.translatorapp.domain.model.Translation
import com.example.translatorapp.domain.repository.TranslationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TranslationRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
) : TranslationRepository {
    private fun userTranslations() = fireStore
        .collection("users")
        .document(firebaseAuth.currentUser!!.uid)
        .collection("translations")

    override suspend fun saveTranslation(translation: Translation) {
        userTranslations()
            .document(translation.id)
            .set(translation.toEntityTranslation())
            .await()
    }

    override suspend fun deleteTranslation(id: String) {
        userTranslations()
            .document(id)
            .delete()
            .await()
    }

    override suspend fun toggleFavourite(id: String, isFavourite: Boolean) {
        userTranslations()
            .document(id)
            .update("isFavourite", isFavourite)
            .await()
    }

    override suspend fun clearHistory() {
        val data = userTranslations()
            .whereEqualTo("isFavourite", false)
            .get()
            .await()

        val batch = fireStore.batch()

        data.documents.forEach { document ->
            batch.delete(document.reference)
        }

        batch.commit().await()
    }

    override fun observeCurrentTranslations(): Flow<List<Translation>> =
        callbackFlow {
            val source = userTranslations()
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)

                        return@addSnapshotListener
                    }

                    val translationsList = snapshot!!.documents
                        .mapNotNull { document ->
                            document.toObject(TranslationEntity::class.java)
                                ?.toDomainTranslation(document.id)
                        }

                    trySend(translationsList)
                }

            awaitClose { source.remove() }
        }
}