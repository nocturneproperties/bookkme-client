package me.bookk.feature.booking.presentation.screen

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.feature.booking.domain.api.entity.BookingAppointmentDetails
import me.bookk.feature.booking.domain.api.entity.BookingOverview
import me.bookk.feature.booking.domain.api.entity.BookingRequestDetails

internal class BookingStatusLabel(
    val text: StringDesc,
    val color: ColorToken,
)

internal fun BookingRequestDetails.Status.label(): BookingStatusLabel {
    val strings = BookingRes.strings
    return when (this) {
        BookingRequestDetails.Status.WAITING_FOR_CONFIRMATION -> {
            BookingStatusLabel(strings.booking_sent_status_waiting.desc(), ColorToken.SecondaryText)
        }

        BookingRequestDetails.Status.CONFIRMED -> {
            BookingStatusLabel(strings.booking_sent_status_confirmed.desc(), ColorToken.Success)
        }

        BookingRequestDetails.Status.DECLINED -> {
            BookingStatusLabel(strings.booking_details_status_declined.desc(), ColorToken.Error)
        }

        BookingRequestDetails.Status.CLOSED -> {
            BookingStatusLabel(
                strings.booking_details_status_closed.desc(),
                ColorToken.SecondaryText,
            )
        }
    }
}

internal fun BookingAppointmentDetails.Status.label(): BookingStatusLabel {
    val strings = BookingRes.strings
    return when (this) {
        BookingAppointmentDetails.Status.CONFIRMED -> {
            BookingStatusLabel(strings.booking_sent_status_confirmed.desc(), ColorToken.Success)
        }

        BookingAppointmentDetails.Status.COMPLETED -> {
            BookingStatusLabel(strings.booking_details_status_done.desc(), ColorToken.SecondaryText)
        }

        BookingAppointmentDetails.Status.CANCELLED -> {
            BookingStatusLabel(strings.booking_details_status_cancelled.desc(), ColorToken.Error)
        }

        BookingAppointmentDetails.Status.MISSED -> {
            BookingStatusLabel(strings.booking_details_status_missed.desc(), ColorToken.Error)
        }
    }
}

internal fun BookingOverview.Status.label(): BookingStatusLabel {
    return when (this) {
        is BookingOverview.Status.Request -> status.label()
        is BookingOverview.Status.Appointment -> status.label()
    }
}
