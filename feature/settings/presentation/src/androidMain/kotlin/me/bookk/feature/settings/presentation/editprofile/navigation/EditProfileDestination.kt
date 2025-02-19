package me.bookk.feature.settings.presentation.editprofile.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.feature.settings.presentation.editprofile.EditProfileEventListener
import me.bookk.feature.settings.presentation.editprofile.EditProfileScreen
import me.bookk.feature.settings.presentation.editprofile.EditProfileViewModel
import me.bookk.feature.settings.presentation.editprofile.LocalEditProfileEventListener
import me.bookk.feature.settings.presentation.navigation.LocalNavigation
import me.bookk.feature.settings.presentation.navigation.SettingsDestination
import me.bookk.feature.settings.presentation.navigation.SettingsNavigation
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.editProfileScreen(navigation: SettingsNavigation) {
    composable<SettingsDestination.EditProfile> {
        val viewModel: EditProfileViewModel = koinViewModel()

        CompositionLocalProvider(
            LocalNavigation provides navigation,
            LocalEditProfileEventListener provides EditProfileEventListener(
                onNameChanged = viewModel::onFirstNameTextChanged,
                onLastNameChanged = viewModel::onLastNameTextChanged,
                onEmailChanged = viewModel::onEmailTextChanged,
                onSaveClick = viewModel::onConfirmButtonClick
            )
        ) {
            SendLifecycleEventsTo(viewModel)
            EditProfileScreen(viewModel.uiState)
        }
    }
}