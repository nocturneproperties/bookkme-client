package me.bookk.feature.booking.presentation.screen.details

import androidx.compose.runtime.Composable
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.rememberViewModel
import me.bookk.feature.booking.presentation.navigation.BookingNavigation
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

@Composable
fun BookingAppointmentDetailsDestination(
    webApp: TelegramWebApp,
    appointmentId: Uuid,
    navigation: BookingNavigation,
) {
    val viewModel = rememberViewModel<BookingAppointmentDetailsViewModel>(appointmentId) {
        parametersOf(appointmentId)
    }
    BookingDetailsHost(webApp, viewModel.uiState, navigation)
}
