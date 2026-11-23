package me.bookk.feature.dashboard.presentation.screen.clienthome

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.rememberViewModel
import me.bookk.site.designsystem.components.ObserveNavigation
import org.koin.core.parameter.parametersOf

@Composable
fun ClientHomeDestination(webApp: TelegramWebApp, onNavigate: (ClientHomeDestinations) -> Unit) {
    val startParameter = webApp.initDataUnsafe.startParam.orEmpty()
    val viewModel = rememberViewModel<ClientHomeViewModel> { parametersOf(startParameter) }
    ObserveTelegramNotifications(webApp, viewModel.uiState.notifications)
    ClientHomeScreen(viewModel.uiState)
    ObserveNavigation(viewModel.uiState.navigation, onNavigate)
}
