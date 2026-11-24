package me.bookk.client.navigation

import kotlin.uuid.Uuid

sealed interface ClientScreen {
    data object Home : ClientScreen
    data class Services(
        val businessId: Uuid,
    ) : ClientScreen

    data class Specialist(
        val businessId: Uuid,
    ) : ClientScreen

    data class DateTime(
        val businessId: Uuid,
    ) : ClientScreen

    data class Review(
        val businessId: Uuid,
    ) : ClientScreen
}
