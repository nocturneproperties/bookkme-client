package me.bookk.feature.services.presentation.service.list

import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class ServiceListDestination : NavigationDestination() {
    data object Back : ServiceListDestination()
    data class AddService(val businessId: Uuid) : ServiceListDestination()

    data object ServiceGroups : ServiceListDestination()
    data class EditService(val id: Uuid) : ServiceListDestination()
}