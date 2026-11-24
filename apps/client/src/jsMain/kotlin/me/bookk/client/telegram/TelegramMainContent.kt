package me.bookk.client.telegram

import androidx.compose.runtime.Composable
import library.telegram.TelegramWebApp
import me.bookk.client.navigation.ClientScreen
import me.bookk.client.navigation.rememberBookingNavigation
import me.bookk.client.navigation.rememberClientHomeNavigation
import me.bookk.core.presentation.navigation.rememberBackStack
import me.bookk.designsystem.components.BackStackHost
import me.bookk.feature.booking.presentation.screen.datetime.BookingDateTimeDestination
import me.bookk.feature.booking.presentation.screen.review.BookingReviewDestination
import me.bookk.feature.booking.presentation.screen.sent.BookingSentDestination
import me.bookk.feature.booking.presentation.screen.services.BookingServicesDestination
import me.bookk.feature.booking.presentation.screen.specialist.BookingSpecialistDestination
import me.bookk.feature.dashboard.presentation.screen.clienthome.ClientHomeDestination

@Composable
internal fun TelegramMainContent(webApp: TelegramWebApp) {
    val stack = rememberBackStack<ClientScreen>(ClientScreen.Home)
    val homeNavigation = rememberClientHomeNavigation(stack)
    val bookingNavigation = rememberBookingNavigation(stack)
    BackStackHost(stack) { screen ->
        when (screen) {
            ClientScreen.Home -> {
                ClientHomeDestination(webApp, homeNavigation)
            }

            is ClientScreen.Services -> {
                BookingServicesDestination(webApp, screen.businessId, bookingNavigation)
            }

            is ClientScreen.Specialist -> {
                BookingSpecialistDestination(webApp, screen.businessId, bookingNavigation)
            }

            is ClientScreen.DateTime -> {
                BookingDateTimeDestination(webApp, screen.businessId, bookingNavigation)
            }

            is ClientScreen.Review -> {
                BookingReviewDestination(webApp, screen.businessId, bookingNavigation)
            }

            is ClientScreen.Sent -> {
                BookingSentDestination(webApp, screen.request, bookingNavigation)
            }
        }
    }
}
