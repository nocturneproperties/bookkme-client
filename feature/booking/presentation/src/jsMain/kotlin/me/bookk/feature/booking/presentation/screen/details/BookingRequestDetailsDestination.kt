package me.bookk.feature.booking.presentation.screen.details

import androidx.compose.runtime.Composable
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.rememberViewModel
import me.bookk.feature.booking.presentation.navigation.BookingNavigation
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

@Composable
fun BookingRequestDetailsDestination(
    webApp: TelegramWebApp,
    requestId: Uuid,
    navigation: BookingNavigation,
) {
    val viewModel = rememberViewModel<BookingRequestDetailsViewModel>(requestId) {
        parametersOf(requestId)
    }
    BookingDetailsHost(webApp, viewModel.uiState, navigation)
}
