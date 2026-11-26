package me.bookk.client.navigation

import me.bookk.core.domain.launch.ConfirmableAction
import me.bookk.feature.authorization.domain.api.StartEmailChange
import me.bookk.feature.booking.domain.api.entity.SentBookingRequest
import kotlin.uuid.Uuid

sealed interface ClientScreen {
    data object Home : ClientScreen

    data object Bookings : ClientScreen
    data class Services(
        val businessId: Uuid,
    ) : ClientScreen

    data class Specialist(
        val businessId: Uuid,
    ) : ClientScreen

    data class DateTime(
        val businessId: Uuid,
    ) : ClientScreen

    data class Review(
        val businessId: Uuid,
    ) : ClientScreen

    data class Sent(
        val request: SentBookingRequest,
    ) : ClientScreen

    data class BookingRequestDetails(
        val requestId: Uuid,
    ) : ClientScreen

    data class BookingAppointmentDetails(
        val appointmentId: Uuid,
    ) : ClientScreen

    data object Settings : ClientScreen

    data object BookingProfile : ClientScreen

    data object ChangeEmail : ClientScreen

    data class VerifyEmail(
        val pendingEmailChange: StartEmailChange.PendingEmailChange,
    ) : ClientScreen

    data object NotificationSettings : ClientScreen

    data object PrivacySettings : ClientScreen

    data object PersonalDataExport : ClientScreen

    data object DeleteAccount : ClientScreen

    data class ConfirmAction(
        val action: ConfirmableAction,
    ) : ClientScreen
}
