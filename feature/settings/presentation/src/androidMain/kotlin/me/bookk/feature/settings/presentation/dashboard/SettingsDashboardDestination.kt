package me.bookk.feature.settings.presentation.dashboard

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.designsystem.components.ObserveNavigation
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
            selectScheme = viewModel::onSchemeSelected,
            performLogout = viewModel::onLogOutClick
        )

        CompositionLocalProvider(
            LocalNavigation provides navigation,
            LocalDashboardEventListener provides listener
        ) {
            SendLifecycleEventsTo(viewModel)
            SettingsDashboardScreen(viewModel.uiState)
            ObserveNavigation(viewModel.uiState.navigation) {
                when (it) {
                    SettingsDashboardDestination.EditProfile -> navigation.navigateToEditProfile()
                }
            }
        }
    }
}