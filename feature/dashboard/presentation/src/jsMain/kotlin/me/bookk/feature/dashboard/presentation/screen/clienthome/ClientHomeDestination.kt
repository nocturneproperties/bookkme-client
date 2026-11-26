package me.bookk.feature.dashboard.presentation.screen.clienthome

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.rememberViewModel
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.feature.dashboard.presentation.navigation.ClientHomeNavigation
import org.koin.core.parameter.parametersOf

@Composable
fun ClientHomeDestination(webApp: TelegramWebApp, navigation: ClientHomeNavigation) {
    val startParameter = webApp.initDataUnsafe.startParam.orEmpty()
    val viewModel = rememberViewModel<ClientHomeViewModel> { parametersOf(startParameter) }
    ObserveTelegramNotifications(webApp, viewModel.uiState.notifications)
    ClientHomeScreen(viewModel.uiState)
    ObserveNavigation(viewModel.uiState.navigation) {
        when (it) {
            ClientHomeDestinations.Settings -> {
                navigation.toSettings()
            }

            ClientHomeDestinations.Bookings -> {
                navigation.toBookings()
            }

            ClientHomeDestinations.Businesses -> {
                navigation.toBusinesses()
            }

            is ClientHomeDestinations.RequestDetails -> {
                navigation.toRequestDetails(it.requestId)
            }

            is ClientHomeDestinations.AppointmentDetails -> {
                navigation.toAppointmentDetails(it.appointmentId)
            }

            is ClientHomeDestinations.Services -> {
                navigation.toServices(it.businessId)
            }

            is ClientHomeDestinations.ConfirmAction -> {
                navigation.toConfirmAction(it.action)
            }
        }
    }
}
