package me.bookk.feature.business.presentation.screen.settings

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.business.presentation.navigation.BusinessDestination
import me.bookk.feature.business.presentation.navigation.BusinessNavigation
import me.bookk.feature.business.presentation.navigation.LocalNavigation
import me.bookk.feature.business.presentation.screen.settings.state.BusinessSettingsDestination
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.settingsScreen(navigation: BusinessNavigation) {
    composable<BusinessDestination.Settings> {
        val viewModel: BusinessSettingsViewModel = koinViewModel()
        val backPressOwner = LocalOnBackPressedDispatcherOwner.current
        val listener = BusinessSettingsEventListener(
            onAddPhotoClick = viewModel::onAddPhotoClicked,
            onNameChanged = viewModel::onNameChanged,
            onDescriptionChanged = viewModel::onDescriptionChanged,
            onSaveClick = viewModel::onSaveClick,
            onViberChanged = viewModel::onViberChanged,
            onAddressChanged = viewModel::onAddressChanged,
            onTelegramChanged = viewModel::onTelegramChanged,
            onCurrencySelected = viewModel::onCurrencySelected,
            onInstagramChanged = viewModel::onInstagramChanged,
            onPickLocationClick = viewModel::onPickLocationClicked,
            onTestLocationClick = viewModel::onTestLocationClick,
            onPhoneChanged = viewModel::onPhoneChanged,
            onBackClick = { backPressOwner?.onBackPressedDispatcher?.onBackPressed() }
        )

        CompositionLocalProvider(
            LocalNavigation provides navigation,
            LocalBusinessSettingsEventListener provides listener
        ) {
            ObserveNotifications(viewModel.uiState.notifications)
            ObserveNavigation(viewModel.uiState.navigation) { destination ->
                when (destination) {
                    BusinessSettingsDestination.Back -> navigation.goBack()
                }
            }
            SendLifecycleEventsTo(viewModel)
            BusinessSettingsScreen(viewModel.uiState)
        }
    }
}