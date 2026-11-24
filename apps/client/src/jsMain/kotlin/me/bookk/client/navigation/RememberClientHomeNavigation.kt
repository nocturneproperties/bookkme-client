package me.bookk.client.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import me.bookk.core.presentation.navigation.BackStack
import me.bookk.feature.dashboard.presentation.navigation.ClientHomeNavigation

@Composable
internal fun rememberClientHomeNavigation(stack: BackStack<ClientScreen>) = remember(stack) {
    ClientHomeNavigation(
        toSettings = {},
        toBookings = {},
        toBusinesses = {},
        toBookingDetails = {},
        toServices = { stack.push(ClientScreen.Services(it)) },
    )
}
