package com.example.translatorapp.presentation.navigation.core

import androidx.navigation3.runtime.NavBackStack
import com.example.translatorapp.presentation.navigation.core.Route

class Navigator(
    private val backStack: NavBackStack<Route>,
    private val onNavigateToRestrictedRoute: (targetRoot: Route?) -> Route,
    private val isLoggedIn: () -> Boolean,
) {
    fun navigate(route: Route) {
        if (route.requiresLogin && !isLoggedIn()) {
            val loginRoute = onNavigateToRestrictedRoute(route)
            backStack.add(loginRoute)
        } else
            backStack.add(route)
    }

    fun goBack() {
        if (backStack.size > 1)
            backStack.removeLastOrNull()
    }

    fun popTo(route: Route) {
        val targetIndex = backStack.indexOfLast { entry ->
            entry == route
        }

        if (targetIndex == -1)
            return

        while (backStack.lastIndex > targetIndex)
            backStack.removeLastOrNull()
    }
}