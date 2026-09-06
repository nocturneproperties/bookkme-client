package me.bookk.feature.business.presentation.navigation

import androidx.navigation.NavGraphBuilder
import me.bookk.feature.business.presentation.screen.dashboard.dashboardScreen
import me.bookk.feature.business.presentation.screen.plugins.pluginsScreen
import me.bookk.feature.business.presentation.screen.settings.settingsScreen

fun NavGraphBuilder.businessGraph(navigation: BusinessNavigation) {
    dashboardScreen(navigation)
    settingsScreen(navigation)
    pluginsScreen(navigation)
}
