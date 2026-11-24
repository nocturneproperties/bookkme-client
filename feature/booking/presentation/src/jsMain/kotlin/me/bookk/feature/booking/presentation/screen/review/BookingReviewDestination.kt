package me.bookk.feature.booking.presentation.screen.review

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.rememberViewModel
import me.bookk.feature.booking.presentation.navigation.BookingNavigation
import me.bookk.site.designsystem.components.ObserveNavigation
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

@Composable
fun BookingReviewDestination(
    webApp: TelegramWebApp,
    businessId: Uuid,
    navigation: BookingNavigation,
) {
    val viewModel = rememberViewModel<BookingReviewViewModel>(businessId) {
        parametersOf(businessId)
    }
    ObserveTelegramNotifications(webApp, viewModel.uiState.notifications)
    BookingReviewScreen(viewModel.uiState, webApp)
    ObserveNavigation(viewModel.uiState.navigation) {
        when (it) {
            BookingReviewDestinations.Back -> navigation.onBack()
            BookingReviewDestinations.Leave -> navigation.onLeave()
            is BookingReviewDestinations.Sent -> navigation.toSent(it.businessId)
        }
    }
}
