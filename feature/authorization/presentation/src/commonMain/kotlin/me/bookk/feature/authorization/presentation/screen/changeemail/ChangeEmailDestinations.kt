package me.bookk.feature.authorization.presentation.screen.changeemail

import me.bookk.core.presentation.navigation.NavigationDestination
import me.bookk.feature.authorization.domain.api.StartEmailChange

sealed class ChangeEmailDestinations : NavigationDestination() {
    data object Back : ChangeEmailDestinations()

    data class VerifyEmail(
        val pendingEmailChange: StartEmailChange.PendingEmailChange,
    ) : ChangeEmailDestinations()
}
