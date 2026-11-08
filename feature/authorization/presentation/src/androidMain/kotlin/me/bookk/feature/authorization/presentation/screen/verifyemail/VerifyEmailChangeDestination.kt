package me.bookk.feature.authorization.presentation.screen.verifyemail

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.authorization.domain.api.StartEmailChange
import me.bookk.feature.authorization.domain.entity.EmailVerification
import me.bookk.feature.authorization.presentation.navigation.EmailChangeDestination
import me.bookk.feature.authorization.presentation.navigation.EmailChangeNavigation
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.Instant
import kotlin.uuid.Uuid

internal fun NavGraphBuilder.verifyEmailChangeScreen(navigation: EmailChangeNavigation) {
    composable<EmailChangeDestination.VerifyEmail> {
        val entry = it.toRoute<EmailChangeDestination.VerifyEmail>()
        val viewModel: VerifyEmailViewModel = koinViewModel {
            parametersOf(VerifyEmailRequest.EmailChange(entry.toPendingEmailChange()))
        }
        ObserveNotifications(viewModel.uiState.notifications)
        SendLifecycleEventsTo(viewModel)
        VerifyEmailScreen(viewModel.uiState)
        ObserveNavigation(viewModel.uiState.navigation) { destination ->
            when (destination) {
                VerifyEmailDestinations.Back -> navigation.navigateBack()
                VerifyEmailDestinations.EmailChanged -> navigation.finishEmailChange()
            }
        }
    }
}

internal fun StartEmailChange.PendingEmailChange.toVerifyEmailDestination(): EmailChangeDestination.VerifyEmail {
    return EmailChangeDestination.VerifyEmail(
        email = email,
        verificationId = verification.id.toString(),
        expiresAtMillis = verification.expiresAt.toEpochMilliseconds(),
        resendAvailableAtMillis = verification.resendAvailableAt.toEpochMilliseconds(),
    )
}

private fun EmailChangeDestination.VerifyEmail.toPendingEmailChange(): StartEmailChange.PendingEmailChange {
    return StartEmailChange.PendingEmailChange(
        email = email,
        verification = EmailVerification(
            id = Uuid.parse(verificationId),
            expiresAt = Instant.fromEpochMilliseconds(expiresAtMillis),
            resendAvailableAt = Instant.fromEpochMilliseconds(resendAvailableAtMillis),
        ),
    )
}
