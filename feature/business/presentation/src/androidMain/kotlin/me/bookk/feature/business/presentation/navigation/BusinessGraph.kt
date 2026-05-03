package me.bookk.feature.business.presentation.navigation

import androidx.navigation.NavGraphBuilder
import me.bookk.feature.business.presentation.clients.list.clientsListScreen
import me.bookk.feature.business.presentation.create.createBusinessScreen
import me.bookk.feature.business.presentation.dashboard.dashboardScreen
import me.bookk.feature.business.presentation.loading.loaderScreen
import me.bookk.feature.business.presentation.settings.settingsScreen

fun NavGraphBuilder.businessGraph(navigation: BusinessNavigation) {
    createBusinessScreen(navigation)
    loaderScreen()
    dashboardScreen(navigation)
    settingsScreen(navigation)
    clientsListScreen(navigation)
}