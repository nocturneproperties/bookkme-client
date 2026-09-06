package me.bookk.feature.business.presentation.navigation

import androidx.compose.runtime.compositionLocalOf
import kotlin.uuid.Uuid

class BusinessNavigation(
    val toAnalytics: () -> Unit = {},
    val toClients: () -> Unit = {},
    val toEmployees: (Uuid) -> Unit = {},
    val toBusinessSettings: (Uuid) -> Unit = {},
    val toBusinessServices: (Uuid) -> Unit = {},
    val toBusinessPlugins: (Uuid) -> Unit = {},
    val toAppointmentSettings: (Uuid) -> Unit = {},
    val toAppointmentHistory: (Uuid) -> Unit = {},
    val toAppointmentRequests: () -> Unit = {},
    val toShopOrders: () -> Unit = {},
    val toShopAssortment: () -> Unit = {},
    val toShopWarehouse: () -> Unit = {},
    val goBack: () -> Unit = {}
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
        toAppointmentRequests = {},
        toBusinessPlugins = {},
        goBack = {}
    )
}