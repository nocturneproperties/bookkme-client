package me.bookk.feature.clients.presentation.edit

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class EditClientDestination : NavigationDestination() {
    data object Back : EditClientDestination()
    data object Deleted : EditClientDestination()
}
