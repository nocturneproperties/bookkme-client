package me.bookk.feature.booking.presentation.screen.details

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.feature.booking.presentation.navigation.BookingNavigation

@Composable
internal fun BookingDetailsHost(
    webApp: TelegramWebApp,
    state: BookingDetailsState,
    navigation: BookingNavigation,
) {
    ObserveTelegramNotifications(webApp, state.notifications)
    BookingDetailsScreen(state, webApp)
    ObserveNavigation(state.navigation) {
        when (it) {
            BookingDetailsDestinations.Back -> navigation.onBack()
            BookingDetailsDestinations.Bookings -> navigation.toBookings()
        }
    }
}
