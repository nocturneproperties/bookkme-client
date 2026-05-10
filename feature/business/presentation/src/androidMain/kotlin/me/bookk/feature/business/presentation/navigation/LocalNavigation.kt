package me.bookk.feature.business.presentation.navigation

import androidx.compose.runtime.compositionLocalOf
import kotlin.uuid.Uuid

class BusinessNavigation(
    val toAnalytics: () -> Unit,
    val toClients: (Uuid) -> Unit,
    val toEmployees: () -> Unit,
    val toBusinessSettings: (Uuid) -> Unit,
    val toBusinessServices: (Uuid) -> Unit,
    val toAppointmentSettings: () -> Unit,
    val toAppointmentHistory: () -> Unit,
    val toAppointmentRequests: () -> Unit,
    val toShopOrders: () -> Unit,
    val toShopAssortment: () -> Unit,
    val toShopWarehouse: () -> Unit,
)

internal val LocalNavigation = compositionLocalOf {
    BusinessNavigation(
        toAnalytics = {},
        toClients = {},
        toEmployees = {},
        toBusinessSettings = {},
        toAppointmentSettings = {},
        toAppointmentHistory = {},
        toBusinessServices = {},
        toShopOrders = {},
        toShopAssortment = {},
        toShopWarehouse = {},
        toAppointmentRequests = {}
    )
}