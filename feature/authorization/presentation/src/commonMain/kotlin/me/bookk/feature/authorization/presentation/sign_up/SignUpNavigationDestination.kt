package me.bookk.feature.authorization.presentation.sign_up

import me.bookk.core.presentation.navigation.NavigationDestination
import me.bookk.feature.authorization.domain.api.StartSignUp

sealed class SignUpNavigationDestination : NavigationDestination() {
    data object Back : SignUpNavigationDestination()

    data class VerifyEmail(val pendingSignUp: StartSignUp.PendingSignUp) : SignUpNavigationDestination()
}
