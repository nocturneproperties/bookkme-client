package me.bookk.feature.business.presentation.settings

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.business.presentation.navigation.BusinessDestination
import me.bookk.feature.business.presentation.navigation.BusinessNavigation
import me.bookk.feature.business.presentation.navigation.LocalNavigation
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

internal fun NavGraphBuilder.settingsScreen(navigation: BusinessNavigation) {
    composable<BusinessDestination.Settings> {
        val entry = it.toRoute<BusinessDestination.Settings>()
        val viewModel: BusinessSettingsViewModel = koinViewModel(parameters = { parametersOf(entry.id) })
        val backPressOwner = LocalOnBackPressedDispatcherOwner.current
        val listener = BusinessSettingsEventListener(
            onNameChanged = viewModel::onNameChanged,
            onDescriptionChanged = viewModel::onDescriptionChanged,
            onSaveClick = viewModel::onSaveClick,
            onViberChanged = viewModel::onViberChanged,
            onAddressChanged = viewModel::onAddressChanged,
            onTelegramChanged = viewModel::onTelegramChanged,
            onCurrencySelected = viewModel::onCurrencySelected,
            onInstagramChanged = viewModel::onInstagramChanged,
            onLocationChanged = viewModel::onLocationChanged,
            onTestLocationClick = viewModel::onTestLocationClick,
            onBackClick = { backPressOwner?.onBackPressedDispatcher?.onBackPressed() }
        )

        CompositionLocalProvider(
            LocalNavigation provides navigation,
            LocalBusinessSettingsEventListener provides listener
        ) {
            ObserveNotifications(viewModel.uiState.notifications)
            SendLifecycleEventsTo(viewModel)
            BusinessSettingsScreen(viewModel.uiState)
        }
    }
}