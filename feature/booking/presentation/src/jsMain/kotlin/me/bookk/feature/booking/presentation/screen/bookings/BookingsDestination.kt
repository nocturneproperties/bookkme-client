package me.bookk.feature.booking.presentation.screen.bookings

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.rememberViewModel
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.feature.booking.presentation.navigation.BookingNavigation

@Composable
fun BookingsDestination(webApp: TelegramWebApp, navigation: BookingNavigation) {
    val viewModel = rememberViewModel<BookingsViewModel>()
    ObserveTelegramNotifications(webApp, viewModel.uiState.notifications)
    BookingsScreen(viewModel.uiState, webApp)
    ObserveNavigation(viewModel.uiState.navigation) {
        when (it) {
            BookingsDestinations.Back -> {
                navigation.onBack()
            }

            is BookingsDestinations.RequestDetails -> {
                navigation.toRequestDetails(it.requestId)
            }

            is BookingsDestinations.AppointmentDetails -> {
                navigation.toAppointmentDetails(it.appointmentId)
            }
        }
    }
}
