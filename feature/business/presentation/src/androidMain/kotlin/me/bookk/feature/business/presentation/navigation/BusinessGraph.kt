package me.bookk.feature.business.presentation.navigation

import androidx.navigation.NavGraphBuilder
import me.bookk.feature.business.presentation.screen.create.createBusinessScreen
import me.bookk.feature.business.presentation.screen.dashboard.dashboardScreen
import me.bookk.feature.business.presentation.screen.loading.loaderScreen
import me.bookk.feature.business.presentation.screen.plugins.pluginsScreen
import me.bookk.feature.business.presentation.screen.settings.settingsScreen

fun NavGraphBuilder.businessGraph(navigation: BusinessNavigation) {
    createBusinessScreen(navigation)
    loaderScreen()
    dashboardScreen(navigation)
    settingsScreen(navigation)
    pluginsScreen(navigation)
}