package me.bookk.feature.authorization.presentation.screen.changeemail

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.rememberViewModel
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.feature.authorization.presentation.navigation.EmailChangeNavigation

@Composable
fun ChangeEmailDestination(webApp: TelegramWebApp, navigation: EmailChangeNavigation) {
    val viewModel = rememberViewModel<ChangeEmailViewModel>()
    ObserveTelegramNotifications(webApp, viewModel.uiState.notifications)
    ChangeEmailScreen(viewModel.uiState, webApp)
    ObserveNavigation(viewModel.uiState.navigation) {
        when (it) {
            ChangeEmailDestinations.Back -> navigation.onBack()

            is ChangeEmailDestinations.VerifyEmail -> navigation.toVerifyEmail(
                it.pendingEmailChange,
            )
        }
    }
}
