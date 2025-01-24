package me.bookk.feature.authorization.presentation.sign_in

import me.bookk.core.presentation.navigation.NavigationDestination

sealed interface SignInNavigationDestination : NavigationDestination {
    data object ToMain : SignInNavigationDestination
    data object ToSignUp : SignInNavigationDestination
    data object Back : SignInNavigationDestination
}