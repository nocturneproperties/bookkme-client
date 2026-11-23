package me.bookk.feature.booking.presentation.screen.specialist

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.rememberViewModel
import me.bookk.site.designsystem.components.ObserveNavigation
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

@Composable
fun BookingSpecialistDestination(
    webApp: TelegramWebApp,
    businessId: Uuid,
    onNavigate: (BookingSpecialistDestinations) -> Unit,
) {
    val viewModel = rememberViewModel<BookingSpecialistViewModel>(businessId) {
        parametersOf(businessId)
    }
    ObserveTelegramNotifications(webApp, viewModel.uiState.notifications)
    BookingSpecialistScreen(viewModel.uiState, webApp)
    ObserveNavigation(viewModel.uiState.navigation, onNavigate)
}
