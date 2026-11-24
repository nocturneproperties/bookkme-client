package me.bookk.feature.booking.presentation.screen.datetime

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.rememberViewModel
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.feature.booking.presentation.navigation.BookingNavigation
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

@Composable
fun BookingDateTimeDestination(
    webApp: TelegramWebApp,
    businessId: Uuid,
    navigation: BookingNavigation,
) {
    val viewModel = rememberViewModel<BookingDateTimeViewModel>(businessId) {
        parametersOf(businessId)
    }
    ObserveTelegramNotifications(webApp, viewModel.uiState.notifications)
    BookingDateTimeScreen(viewModel.uiState, webApp)
    ObserveNavigation(viewModel.uiState.navigation) {
        when (it) {
            BookingDateTimeDestinations.Back -> navigation.onBack()
            BookingDateTimeDestinations.Leave -> navigation.onLeave()
            is BookingDateTimeDestinations.Review -> navigation.toReview(it.businessId)
        }
    }
}
