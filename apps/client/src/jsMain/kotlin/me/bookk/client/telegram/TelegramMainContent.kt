package me.bookk.client.telegram

import androidx.compose.runtime.Composable
import library.telegram.TelegramWebApp
import me.bookk.client.navigation.ClientScreen
import me.bookk.core.presentation.navigation.BackStack
import me.bookk.core.presentation.navigation.BackStackHost
import me.bookk.core.presentation.navigation.rememberBackStack
import me.bookk.feature.booking.presentation.screen.services.BookingServicesDestination
import me.bookk.feature.booking.presentation.screen.services.BookingServicesDestinations
import me.bookk.feature.dashboard.presentation.screen.clienthome.ClientHomeDestination
import me.bookk.feature.dashboard.presentation.screen.clienthome.ClientHomeDestinations

@Composable
internal fun TelegramMainContent(webApp: TelegramWebApp) {
    val stack = rememberBackStack<ClientScreen>(ClientScreen.Home)
    BackStackHost(stack) { screen ->
        when (screen) {
            ClientScreen.Home -> {
                ClientHomeDestination(webApp) { stack.navigateFromHome(it) }
            }

            is ClientScreen.Services -> {
                BookingServicesDestination(webApp, screen.businessId) {
                    stack.navigateFromServices(it)
                }
            }
        }
    }
}

private fun BackStack<ClientScreen>.navigateFromHome(destination: ClientHomeDestinations) {
    when (destination) {
        is ClientHomeDestinations.Services -> push(ClientScreen.Services(destination.businessId))

        ClientHomeDestinations.Settings,
        ClientHomeDestinations.Bookings,
        ClientHomeDestinations.Businesses,
        is ClientHomeDestinations.BookingDetails,
        -> Unit
    }
}

private fun BackStack<ClientScreen>.navigateFromServices(destination: BookingServicesDestinations) {
    when (destination) {
        BookingServicesDestinations.Back -> pop()
        is BookingServicesDestinations.Specialist -> Unit
    }
}
