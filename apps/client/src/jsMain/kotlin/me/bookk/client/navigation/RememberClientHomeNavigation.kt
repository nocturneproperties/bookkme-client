package me.bookk.client.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import me.bookk.core.presentation.navigation.BackStack
import me.bookk.feature.dashboard.presentation.navigation.ClientHomeNavigation

@Composable
internal fun rememberClientHomeNavigation(stack: BackStack<ClientScreen>) = remember(stack) {
    ClientHomeNavigation(
        toSettings = { stack.openSettings() },
        toBookings = { stack.push(ClientScreen.Bookings) },
        toBusinesses = {},
        toRequestDetails = { stack.push(ClientScreen.BookingRequestDetails(it)) },
        toAppointmentDetails = { stack.push(ClientScreen.BookingAppointmentDetails(it)) },
        toServices = { stack.push(ClientScreen.Services(it)) },
        toConfirmAction = { stack.push(ClientScreen.ConfirmAction(it)) },
    )
}

private fun BackStack<ClientScreen>.openSettings() {
    if (current != ClientScreen.Settings) push(ClientScreen.Settings)
}
