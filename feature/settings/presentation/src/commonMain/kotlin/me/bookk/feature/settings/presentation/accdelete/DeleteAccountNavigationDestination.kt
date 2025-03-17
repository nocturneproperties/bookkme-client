package me.bookk.feature.settings.presentation.accdelete

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class DeleteAccountNavigationDestination: NavigationDestination() {
    data object SignIn : DeleteAccountNavigationDestination()
}