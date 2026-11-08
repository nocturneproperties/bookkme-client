package me.bookk.feature.authorization.presentation.screen.verifyemail

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class VerifyEmailDestinations : NavigationDestination() {
    data object Back : VerifyEmailDestinations()
    data object EmailChanged : VerifyEmailDestinations()
}
