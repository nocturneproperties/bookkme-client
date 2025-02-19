package me.bookk.feature.authorization.presentation.sign_up

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class SignUpNavigationDestination : NavigationDestination() {
    data object Main : SignUpNavigationDestination()
}