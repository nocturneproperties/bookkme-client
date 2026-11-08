package me.bookk.feature.authorization.presentation.screen.changeemail

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.authorization.presentation.navigation.EmailChangeDestination
import me.bookk.feature.authorization.presentation.navigation.EmailChangeNavigation
import me.bookk.feature.authorization.presentation.screen.verifyemail.toVerifyEmailDestination
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.changeEmailScreen(navigation: EmailChangeNavigation) {
    composable<EmailChangeDestination.ChangeEmail> {
        val viewModel: ChangeEmailViewModel = koinViewModel()
        ObserveNotifications(viewModel.uiState.notifications)
        SendLifecycleEventsTo(viewModel)
        ChangeEmailScreen(viewModel.uiState)
        ObserveNavigation(viewModel.uiState.navigation) { destination ->
            when (destination) {
                ChangeEmailDestinations.Back -> navigation.navigateBack()
                is ChangeEmailDestinations.VerifyEmail -> {
                    navigation.navigateToVerifyEmail(destination.pendingEmailChange.toVerifyEmailDestination())
                }
            }
        }
    }
}
