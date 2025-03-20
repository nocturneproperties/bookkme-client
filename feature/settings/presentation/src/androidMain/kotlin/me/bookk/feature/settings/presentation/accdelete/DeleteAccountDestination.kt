package me.bookk.feature.settings.presentation.accdelete

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.feature.settings.presentation.navigation.LocalNavigation
import me.bookk.feature.settings.presentation.navigation.SettingsDestination
import me.bookk.feature.settings.presentation.navigation.SettingsNavigation
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.deleteAccountScreen(navigation: SettingsNavigation) {
    composable<SettingsDestination.DeleteAccount> {
        val viewModel: DeleteAccountViewModel = koinViewModel()
        val listener = DeleteAccountEventListener(
            onDeleteClick = viewModel::onDeleteClick,
            onSwitchStateChanged = viewModel::onSwitchStateChanged,
        )

        CompositionLocalProvider(
            LocalNavigation provides navigation,
            LocalDeleteAccountEventListener provides listener
        ) {
            SendLifecycleEventsTo(viewModel)
            DeleteAccountScreen(viewModel.uiState)
        }
    }
}