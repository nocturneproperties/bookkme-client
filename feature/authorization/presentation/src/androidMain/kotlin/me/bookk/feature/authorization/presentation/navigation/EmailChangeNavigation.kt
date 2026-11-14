package me.bookk.feature.authorization.presentation.navigation

import androidx.navigation.NavGraphBuilder
import kotlinx.serialization.Serializable
import me.bookk.feature.authorization.presentation.screen.changeemail.changeEmailScreen
import me.bookk.feature.authorization.presentation.screen.verifyemail.verifyEmailChangeScreen

class EmailChangeNavigation(
    val navigateBack: () -> Unit,
    val navigateToVerifyEmail: (EmailChangeDestination.VerifyEmail) -> Unit,
    val finishEmailChange: () -> Unit,
)

sealed class EmailChangeDestination {
    @Serializable
    data object ChangeEmail : EmailChangeDestination()

    @Serializable
    data class VerifyEmail(
        val email: String,
        val verificationId: String,
        val expiresAtMillis: Long,
        val resendAvailableAtMillis: Long,
    ) : EmailChangeDestination()
}

fun NavGraphBuilder.emailChangeGraph(navigation: EmailChangeNavigation) {
    changeEmailScreen(navigation)
    verifyEmailChangeScreen(navigation)
}
