package me.bookk.client.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import me.bookk.core.presentation.navigation.BackStack
import me.bookk.feature.booking.presentation.navigation.BookingNavigation

@Composable
internal fun rememberBookingNavigation(stack: BackStack<ClientScreen>) = remember(stack) {
    BookingNavigation(
        onBack = { stack.pop() },
        onLeave = { stack.popToRoot() },
        toSpecialist = { stack.push(ClientScreen.Specialist(it)) },
        toDateTime = { stack.push(ClientScreen.DateTime(it)) },
        toReview = { stack.push(ClientScreen.Review(it)) },
        toSent = { stack.popToRoot() },
    )
}
