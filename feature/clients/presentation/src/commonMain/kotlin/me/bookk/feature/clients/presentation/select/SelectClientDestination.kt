package me.bookk.feature.clients.presentation.select

import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class SelectClientDestination : NavigationDestination() {
    data object Back : SelectClientDestination()
    data class AddClient(
        val businessId: Uuid,
    ) : SelectClientDestination()
    data class Selected(
        val clientId: Uuid,
    ) : SelectClientDestination()
}
