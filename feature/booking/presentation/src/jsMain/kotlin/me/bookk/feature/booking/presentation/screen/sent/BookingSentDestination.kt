package me.bookk.feature.booking.presentation.screen.sent

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.rememberViewModel
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.feature.booking.domain.api.entity.SentBookingRequest
import me.bookk.feature.booking.presentation.navigation.BookingNavigation
import org.koin.core.parameter.parametersOf

@Composable
fun BookingSentDestination(
    webApp: TelegramWebApp,
    request: SentBookingRequest,
    navigation: BookingNavigation,
) {
    val viewModel = rememberViewModel<BookingSentViewModel>(request) {
        parametersOf(request)
    }
    ObserveTelegramNotifications(webApp, viewModel.uiState.notifications)
    BookingSentScreen(viewModel.uiState, webApp)
    ObserveNavigation(viewModel.uiState.navigation) {
        when (it) {
            BookingSentDestinations.Home -> {
                navigation.onLeave()
            }

            is BookingSentDestinations.Services -> {
                navigation.toNewBooking(it.businessId)
            }

            is BookingSentDestinations.RequestDetails -> {
                navigation.toRequestDetails(it.requestId)
            }

            is BookingSentDestinations.AppointmentDetails -> {
                navigation.toAppointmentDetails(it.appointmentId)
            }
        }
    }
}
