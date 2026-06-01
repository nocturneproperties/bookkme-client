package me.bookk.feature.services.presentation.group.list

import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class ServiceGroupListDestination : NavigationDestination() {

    data class AddGroup(val businessId: Uuid) : ServiceGroupListDestination()
    data object Back : ServiceGroupListDestination()
}
