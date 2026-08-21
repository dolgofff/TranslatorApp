package com.example.translatorapp.data.repository

import androidx.datastore.core.DataStore
import com.example.translatorapp.data.datastore.LanguagePreferences
import com.example.translatorapp.domain.model.language.LanguageCode
import com.example.translatorapp.domain.model.language.LanguageSavedSettings
import com.example.translatorapp.domain.repository.GlobalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlobalRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<LanguagePreferences>,
) : GlobalRepository {
    override val preferencesFlow: Flow<LanguageSavedSettings> =
        dataStore.data.catch { emit(LanguagePreferences()) }.map { prefs ->
            LanguageSavedSettings(
                savedSourceLanguage = LanguageCode.fromCode(prefs.sourceLanguageCode)
                    ?: LanguageCode.ENGLISH,
                savedDestinationLanguage = LanguageCode.fromCode(prefs.destinationLanguageCode)
                    ?: LanguageCode.RUSSIAN
            )
        }

    override suspend fun setSourceLanguage(code: String) {
        dataStore.updateData { it.copy(sourceLanguageCode = code) }
    }

    override suspend fun setDestinationLanguage(code: String) {
        dataStore.updateData { it.copy(destinationLanguageCode = code) }
    }
}