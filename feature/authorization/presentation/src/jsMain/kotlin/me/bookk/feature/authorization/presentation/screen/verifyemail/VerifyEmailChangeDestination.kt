package me.bookk.feature.authorization.presentation.screen.verifyemail

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.rememberViewModel
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.feature.authorization.domain.api.StartEmailChange
import me.bookk.feature.authorization.presentation.navigation.EmailChangeNavigation
import org.koin.core.parameter.parametersOf

@Composable
fun VerifyEmailChangeDestination(
    webApp: TelegramWebApp,
    pendingEmailChange: StartEmailChange.PendingEmailChange,
    navigation: EmailChangeNavigation,
) {
    val viewModel = rememberViewModel<VerifyEmailViewModel>(pendingEmailChange) {
        parametersOf(VerifyEmailRequest.EmailChange(pendingEmailChange))
    }
    ObserveTelegramNotifications(webApp, viewModel.uiState.notifications)
    VerifyEmailScreen(viewModel.uiState, webApp)
    ObserveNavigation(viewModel.uiState.navigation) {
        when (it) {
            VerifyEmailDestinations.Back -> navigation.onBack()
            VerifyEmailDestinations.EmailChanged -> navigation.onEmailChanged()
        }
    }
}
