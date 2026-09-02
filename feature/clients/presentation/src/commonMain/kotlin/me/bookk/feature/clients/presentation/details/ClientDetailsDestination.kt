package me.bookk.feature.clients.presentation.details

import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class ClientDetailsDestination : NavigationDestination() {
    data object Back : ClientDetailsDestination()
    data class Edit(val id: Uuid) : ClientDetailsDestination()
}