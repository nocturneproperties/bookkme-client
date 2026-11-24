package me.bookk.feature.booking.presentation.screen.specialist

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.rememberViewModel
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.feature.booking.presentation.navigation.BookingNavigation
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

@Composable
fun BookingSpecialistDestination(
    webApp: TelegramWebApp,
    businessId: Uuid,
    navigation: BookingNavigation,
) {
    val viewModel = rememberViewModel<BookingSpecialistViewModel>(businessId) {
        parametersOf(businessId)
    }
    ObserveTelegramNotifications(webApp, viewModel.uiState.notifications)
    BookingSpecialistScreen(viewModel.uiState, webApp)
    ObserveNavigation(viewModel.uiState.navigation) {
        when (it) {
            BookingSpecialistDestinations.Back -> navigation.onBack()
            is BookingSpecialistDestinations.DateTime -> navigation.toDateTime(it.businessId)
        }
    }
}
