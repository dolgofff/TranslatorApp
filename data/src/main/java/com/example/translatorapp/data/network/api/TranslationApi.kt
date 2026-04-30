package com.example.translatorapp.data.network.api

import com.example.translatorapp.data.network.dto.TranslationResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TranslationApi {
    @GET("translate")
    suspend fun translate(
        @Query("sl") sourceLanguage: String,
        @Query("dl") destinationLanguage: String,
        @Query("text") text: String,
    ): TranslationResponse
}



