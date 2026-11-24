package me.bookk.feature.booking.presentation.screen.services

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.rememberViewModel
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.feature.booking.presentation.navigation.BookingNavigation
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

@Composable
fun BookingServicesDestination(
    webApp: TelegramWebApp,
    businessId: Uuid,
    navigation: BookingNavigation,
) {
    val viewModel = rememberViewModel<BookingServicesViewModel>(businessId) {
        parametersOf(businessId)
    }
    ObserveTelegramNotifications(webApp, viewModel.uiState.notifications)
    BookingServicesScreen(viewModel.uiState, webApp)
    ObserveNavigation(viewModel.uiState.navigation) {
        when (it) {
            BookingServicesDestinations.Back -> navigation.onBack()
            is BookingServicesDestinations.Specialist -> navigation.toSpecialist(it.businessId)
        }
    }
}
