package me.bookk.feature.services.presentation.service.edit

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class EditServiceDestination : NavigationDestination() {
    data object Back : EditServiceDestination()
}
