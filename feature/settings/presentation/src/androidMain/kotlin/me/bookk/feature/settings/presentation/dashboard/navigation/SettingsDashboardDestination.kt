package me.bookk.feature.settings.presentation.dashboard.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.feature.settings.presentation.dashboard.DashboardEventListener
import me.bookk.feature.settings.presentation.dashboard.LocalDashboardEventListener
import me.bookk.feature.settings.presentation.dashboard.SettingsDashboardScreen
import me.bookk.feature.settings.presentation.dashboard.SettingsDashboardViewModel
import me.bookk.feature.settings.presentation.navigation.LocalNavigation
import me.bookk.feature.settings.presentation.navigation.SettingsDestination
import me.bookk.feature.settings.presentation.navigation.SettingsNavigation
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.dashboardScreen(navigation: SettingsNavigation) {
    composable<SettingsDestination.Dashboard> {
        val viewModel: SettingsDashboardViewModel = koinViewModel()
        val listener = DashboardEventListener(
            showTerms = viewModel::showTerms,
            showPolicy = viewModel::showPolicy,
            selectScheme = viewModel::onSchemeSelected
        )

        CompositionLocalProvider(
            LocalNavigation provides navigation,
            LocalDashboardEventListener provides listener
        ) {
            SettingsDashboardScreen(viewModel.uiState)
        }
    }
}