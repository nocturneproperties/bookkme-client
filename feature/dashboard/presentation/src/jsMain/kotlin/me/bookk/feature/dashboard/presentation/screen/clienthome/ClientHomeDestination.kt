package me.bookk.feature.dashboard.presentation.screen.clienthome

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.rememberViewModel
import me.bookk.feature.dashboard.presentation.navigation.ClientHomeNavigation
import me.bookk.site.designsystem.components.ObserveNavigation
import org.koin.core.parameter.parametersOf

@Composable
fun ClientHomeDestination(webApp: TelegramWebApp, navigation: ClientHomeNavigation) {
    val startParameter = webApp.initDataUnsafe.startParam.orEmpty()
    val viewModel = rememberViewModel<ClientHomeViewModel> { parametersOf(startParameter) }
    ObserveTelegramNotifications(webApp, viewModel.uiState.notifications)
    ClientHomeScreen(viewModel.uiState)
    ObserveNavigation(viewModel.uiState.navigation) {
        when (it) {
            ClientHomeDestinations.Settings -> navigation.toSettings()
            ClientHomeDestinations.Bookings -> navigation.toBookings()
            ClientHomeDestinations.Businesses -> navigation.toBusinesses()
            is ClientHomeDestinations.BookingDetails -> navigation.toBookingDetails(it.bookingId)
            is ClientHomeDestinations.Services -> navigation.toServices(it.businessId)
        }
    }
}
