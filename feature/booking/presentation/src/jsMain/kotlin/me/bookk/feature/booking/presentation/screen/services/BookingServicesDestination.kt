package me.bookk.feature.booking.presentation.screen.services

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.rememberViewModel
import me.bookk.site.designsystem.components.ObserveNavigation
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

@Composable
fun BookingServicesDestination(
    webApp: TelegramWebApp,
    businessId: Uuid,
    onNavigate: (BookingServicesDestinations) -> Unit,
) {
    val viewModel = rememberViewModel<BookingServicesViewModel>(businessId) {
        parametersOf(businessId)
    }
    ObserveTelegramNotifications(webApp, viewModel.uiState.notifications)
    BookingServicesScreen(viewModel.uiState, webApp)
    ObserveNavigation(viewModel.uiState.navigation, onNavigate)
}
