package me.bookk.feature.authorization.presentation.navigation

import me.bookk.feature.authorization.domain.api.StartEmailChange

class EmailChangeNavigation(
    val onBack: () -> Unit,
    val toVerifyEmail: (StartEmailChange.PendingEmailChange) -> Unit,
    val onEmailChanged: () -> Unit,
)
