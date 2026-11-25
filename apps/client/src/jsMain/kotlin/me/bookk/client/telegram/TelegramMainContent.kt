package me.bookk.client.telegram

import androidx.compose.runtime.Composable
import library.telegram.TelegramSettingsButton
import library.telegram.TelegramWebApp
import me.bookk.client.navigation.ClientScreen
import me.bookk.client.navigation.rememberBookingNavigation
import me.bookk.client.navigation.rememberClientHomeNavigation
import me.bookk.client.navigation.rememberSettingsNavigation
import me.bookk.core.presentation.navigation.rememberBackStack
import me.bookk.designsystem.components.BackStackHost
import me.bookk.feature.booking.presentation.screen.bookings.BookingsDestination
import me.bookk.feature.booking.presentation.screen.datetime.BookingDateTimeDestination
import me.bookk.feature.booking.presentation.screen.details.BookingAppointmentDetailsDestination
import me.bookk.feature.booking.presentation.screen.details.BookingRequestDetailsDestination
import me.bookk.feature.booking.presentation.screen.review.BookingReviewDestination
import me.bookk.feature.booking.presentation.screen.sent.BookingSentDestination
import me.bookk.feature.booking.presentation.screen.services.BookingServicesDestination
import me.bookk.feature.booking.presentation.screen.specialist.BookingSpecialistDestination
import me.bookk.feature.dashboard.presentation.screen.clienthome.ClientHomeDestination
import me.bookk.feature.settings.presentation.dashboard.SettingsDestination

@Composable
internal fun TelegramMainContent(webApp: TelegramWebApp) {
    val stack = rememberBackStack<ClientScreen>(ClientScreen.Home)
    val homeNavigation = rememberClientHomeNavigation(stack)
    val bookingNavigation = rememberBookingNavigation(stack)
    val settingsNavigation = rememberSettingsNavigation(stack, webApp)
    TelegramSettingsButton(webApp, homeNavigation.toSettings)
    BackStackHost(stack) { screen ->
        when (screen) {
            ClientScreen.Home -> {
                ClientHomeDestination(webApp, homeNavigation)
            }

            ClientScreen.Bookings -> {
                BookingsDestination(webApp, bookingNavigation)
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

            is ClientScreen.BookingRequestDetails -> {
                BookingRequestDetailsDestination(webApp, screen.requestId, bookingNavigation)
            }

            is ClientScreen.BookingAppointmentDetails -> {
                BookingAppointmentDetailsDestination(
                    webApp,
                    screen.appointmentId,
                    bookingNavigation,
                )
            }

            ClientScreen.Settings -> {
                SettingsDestination(webApp, settingsNavigation)
            }

            ClientScreen.BookingProfile -> {}

            ClientScreen.NotificationSettings -> {}

            ClientScreen.PrivacySettings -> {}

            ClientScreen.PersonalDataExport -> {}

            ClientScreen.DeleteAccount -> {}
        }
    }
}
