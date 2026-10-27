package me.bookk.feature.clients.presentation.create

import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class CreateClientDestination : NavigationDestination() {
    data class Details(val id: Uuid) : CreateClientDestination()
    data object Back : CreateClientDestination()
}