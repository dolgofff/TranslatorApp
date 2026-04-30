package com.example.translatorapp.data.datastore

import android.content.Context
import androidx.datastore.dataStore

val Context.languageDataStore by dataStore(
    fileName = "prefs.json",
    serializer = LanguagePreferencesSerializer
)