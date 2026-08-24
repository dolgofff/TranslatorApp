package com.example.translatorapp.domain.model.base

data class User(
    val id: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
)