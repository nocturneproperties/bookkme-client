package me.bookk.feature.authorization.presentation.screen.verifyemail

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.authorization.domain.api.StartSignUp
import me.bookk.feature.authorization.presentation.navigation.AuthDestination
import me.bookk.feature.authorization.presentation.navigation.AuthNavigation
import me.bookk.feature.authorization.presentation.navigation.LocalNavigation
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.Instant

internal fun NavGraphBuilder.verifyEmailScreen(navigation: AuthNavigation) {
    composable<AuthDestination.VerifyEmail> {
        val entry = it.toRoute<AuthDestination.VerifyEmail>()
        val viewModel: VerifyEmailViewModel = koinViewModel {
            parametersOf(VerifyEmailRequest.SignUp(entry.toPendingSignUp()))
        }
        CompositionLocalProvider(LocalNavigation provides navigation) {
            ObserveNotifications(viewModel.uiState.notifications)
            SendLifecycleEventsTo(viewModel)
            VerifyEmailScreen(viewModel.uiState)
        }
        ObserveNavigation(viewModel.uiState.navigation) { destination ->
            when (destination) {
                VerifyEmailDestinations.Back -> navigation.navigateBack()
                VerifyEmailDestinations.EmailChanged -> Unit
            }
        }
    }
}

internal fun StartSignUp.PendingSignUp.toVerifyEmailDestination(): AuthDestination.VerifyEmail {
    return AuthDestination.VerifyEmail(
        maskedEmail = maskedEmail,
        expiresAtMillis = expiresAt.toEpochMilliseconds(),
        resendAvailableAtMillis = resendAvailableAt.toEpochMilliseconds(),
    )
}

private fun AuthDestination.VerifyEmail.toPendingSignUp(): StartSignUp.PendingSignUp {
    return StartSignUp.PendingSignUp(
        maskedEmail = maskedEmail,
        expiresAt = Instant.fromEpochMilliseconds(expiresAtMillis),
        resendAvailableAt = Instant.fromEpochMilliseconds(resendAvailableAtMillis),
    )
}
