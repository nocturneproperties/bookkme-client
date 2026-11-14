package me.bookk.feature.authorization.presentation.navigation

import kotlinx.serialization.Serializable

sealed class AuthDestination {
    @Serializable
    data object SignUp : AuthDestination()

    @Serializable
    data object SignIn : AuthDestination()

    @Serializable
    data object Troubleshoot : AuthDestination()

    @Serializable
    data class VerifyEmail(
        val maskedEmail: String,
        val expiresAtMillis: Long,
        val resendAvailableAtMillis: Long,
    ) : AuthDestination()
}
