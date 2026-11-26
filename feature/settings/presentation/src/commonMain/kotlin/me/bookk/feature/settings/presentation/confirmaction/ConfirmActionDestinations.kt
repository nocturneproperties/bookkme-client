package me.bookk.feature.settings.presentation.confirmaction

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class ConfirmActionDestinations : NavigationDestination() {
    data object Back : ConfirmActionDestinations()
    data object Settings : ConfirmActionDestinations()
    data object PersonalDataExport : ConfirmActionDestinations()
}
