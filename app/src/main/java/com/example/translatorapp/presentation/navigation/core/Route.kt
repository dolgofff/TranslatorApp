package com.example.translatorapp.presentation.navigation.core

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Route(val requiresLogin: Boolean = false) : NavKey {
    @Serializable
    data class LoginRoute(val redirectTo: Route? = null) : Route()

    @Serializable
    data class RegistrationRoute(val redirectTo: Route? = null) : Route()

    @Serializable
    data object TranslationRoute : Route(requiresLogin = true)

    @Serializable
    data object HistoryRoute : Route(requiresLogin = true)

    @Serializable
    data object FavouritesRoute : Route(requiresLogin = true)
}