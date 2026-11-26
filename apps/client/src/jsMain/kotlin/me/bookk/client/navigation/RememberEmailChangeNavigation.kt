package me.bookk.client.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import me.bookk.core.presentation.navigation.BackStack
import me.bookk.feature.authorization.presentation.navigation.EmailChangeNavigation

@Composable
internal fun rememberEmailChangeNavigation(stack: BackStack<ClientScreen>): EmailChangeNavigation {
    return remember(stack) {
        EmailChangeNavigation(
            onBack = { stack.pop() },
            toVerifyEmail = { stack.push(ClientScreen.VerifyEmail(it)) },
            onEmailChanged = { stack.popTo(ClientScreen.BookingProfile) },
        )
    }
}
