package com.example.translatorapp.data.datastore

import android.util.Log
import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object LanguagePreferencesSerializer : Serializer<LanguagePreferences> {
    private val json = Json { ignoreUnknownKeys = true }
    override val defaultValue: LanguagePreferences
        get() = LanguagePreferences()

    override suspend fun readFrom(input: InputStream): LanguagePreferences {
        return try {
            json.decodeFromString(
                deserializer = LanguagePreferences.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (exc: Exception) {
            Log.e("DataStore", "Error while reading languages from dataStore", exc)
            defaultValue
        }
    }

    override suspend fun writeTo(t: LanguagePreferences, output: OutputStream) {
        output.write(
            json.encodeToString(
                serializer = LanguagePreferences.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}