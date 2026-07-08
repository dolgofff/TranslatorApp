package com.example.translatorapp.presentation.navigation.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.serialization.NavBackStackSerializer
import androidx.navigation3.runtime.serialization.NavKeySerializer
import androidx.navigation3.ui.NavDisplay
import com.example.translatorapp.presentation.navigation.auth.AuthState
import com.example.translatorapp.presentation.screen.camera.CameraScreen
import com.example.translatorapp.presentation.screen.favourites.FavouritesScreen
import com.example.translatorapp.presentation.screen.history.HistoryScreen
import com.example.translatorapp.presentation.screen.login.LoginScreen
import com.example.translatorapp.presentation.screen.registration.RegistrationScreen
import com.example.translatorapp.presentation.screen.translation.main.TranslationScreen

@Composable
fun NavigationRoot(authState: AuthState) {

    when (authState) {
        AuthState.Loading -> {}

        AuthState.LoggedOut -> {
            AuthNavHost(startDestination = Route.LoginRoute())
        }

        AuthState.LoggedIn -> {
            MainNavHost(startDestination = Route.TranslationRoute)
        }
    }
}

@Composable
private fun <T : NavKey> rememberNavBackStack(
    vararg elements: T,
    key: Any? = Unit,
): NavBackStack<T> {
    val backStack = remember(key) {
        NavBackStack(*elements)
    }

    return rememberSerializable(
        serializer = NavBackStackSerializer(elementSerializer = NavKeySerializer())
    ) {
        backStack
    }
}

@Composable
private fun AuthNavHost(startDestination: Route) {
    val backStack = rememberNavBackStack(startDestination)

    val navigator = remember(backStack) {
        Navigator(
            backStack = backStack,
            isLoggedIn = { false },
            onNavigateToRestrictedRoute = { target ->
                Route.LoginRoute(redirectTo = target)
            }
        )
    }

    NavDisplay(
        backStack = backStack,
        onBack = { navigator.goBack() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {

            entry<Route.LoginRoute> { key ->
                LoginScreen(
                    onSignInClick = {
                        backStack.remove(key)
                        navigator.navigate(key.redirectTo ?: Route.TranslationRoute)
                    },
                    onSignUpNavClick = {
                        navigator.navigate(Route.RegistrationRoute())
                    }
                )
            }

            entry<Route.RegistrationRoute> { key ->
                RegistrationScreen(
                    onSignUpClick = {
                        backStack.remove(key)
                        navigator.navigate(key.redirectTo ?: Route.TranslationRoute)
                    },
                    onSignInNavClick = {
                        navigator.navigate(Route.LoginRoute())
                    }
                )
            }
        }
    )
}

@Composable
private fun MainNavHost(startDestination: Route) {
    val backStack = rememberNavBackStack(startDestination)

    val navigator = remember(backStack) {
        Navigator(
            backStack = backStack,
            isLoggedIn = { true },
            onNavigateToRestrictedRoute = { it ?: Route.LoginRoute() }
        )
    }

    NavDisplay(
        backStack = backStack,
        onBack = { navigator.goBack() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {

            entry<Route.TranslationRoute> {
                TranslationScreen(
                    onHistoryNavClick = {
                        navigator.navigate(Route.HistoryRoute)
                    },
                    onFavouritesNavClick = {
                        navigator.navigate(Route.FavouritesRoute)
                    }
                )
            }

            entry<Route.HistoryRoute> {
                HistoryScreen(
                    onNavBackClick = { navigator.goBack() }
                )
            }

            entry<Route.FavouritesRoute> {
                FavouritesScreen(
                    onNavBackClick = { navigator.goBack() }
                )
            }

            entry<Route.CameraRoute> {
                CameraScreen(
                    onNavBackClick = { navigator.goBack() }
                )
            }
        }
    )
}