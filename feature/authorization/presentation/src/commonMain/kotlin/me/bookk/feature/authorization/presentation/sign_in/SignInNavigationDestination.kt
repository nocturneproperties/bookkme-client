package me.bookk.feature.authorization.presentation.sign_in

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class SignInNavigationDestination : NavigationDestination() {
    data object ToMain : SignInNavigationDestination()
}