package me.bookk.feature.business.presentation.navigation

import androidx.compose.runtime.compositionLocalOf

class BusinessNavigation(
    val toAnalytics: () -> Unit,
    val toClients: () -> Unit,
    val toEmployees: () -> Unit,
    val toBusinessSettings: () -> Unit,
    val toAppointmentSettings: () -> Unit,
    val toAppointmentHistory: () -> Unit,
    val toAppointmentServices: () -> Unit,
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
        toAppointmentServices = {},
        toShopOrders = {},
        toShopAssortment = {},
        toShopWarehouse = {}
    )
}