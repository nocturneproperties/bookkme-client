package me.bookk.feature.booking.presentation.screen.details

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.flow.flowOn
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.feature.booking.domain.api.GetBookingAppointmentDetails
import me.bookk.feature.booking.domain.api.entity.BookingAppointmentDetails
import me.bookk.feature.booking.domain.api.entity.BookingAppointmentDetails.Status
import me.bookk.feature.booking.presentation.BookingStateFactory
import me.bookk.feature.booking.presentation.screen.label
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class BookingAppointmentDetailsViewModel(
    @InjectedParam private val appointmentId: Uuid,
    private val getBookingAppointmentDetails: GetBookingAppointmentDetails,
    dateLocalizer: DateLocalizer,
    stateFactory: BookingStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    private val items = BookingDetailsItems(dateLocalizer)

    val uiState: BookingDetailsState = stateFactory.createBookingDetailsState().setup()

    init {
        observeAppointment()
    }

    private fun BookingDetailsState.setup() = apply {
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(BookingDetailsDestinations.Back)
        }
        actionButton.isVisible = false
    }

    private fun observeAppointment() {
        getBookingAppointmentDetails.flow(appointmentId)
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderAppointment(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun renderAppointment(details: BookingAppointmentDetails?) {
        when (details) {
            null -> uiState.navigation.push(BookingDetailsDestinations.Back)
            else -> uiState.booking = details.toItem()
        }
    }

    private fun BookingAppointmentDetails.toItem(): BookingDetailsItem {
        return items.item(summary, status.presentation(summary.businessName))
    }

    private fun Status.presentation(businessName: String): BookingStatusPresentation {
        val label = label()
        return BookingStatusPresentation(label.text, label.color, explanation(businessName))
    }

    private fun Status.explanation(businessName: String): StringDesc {
        val strings = BookingRes.strings
        return when (this) {
            Status.CONFIRMED -> strings.booking_details_confirmed_explanation.format(businessName)
            Status.COMPLETED -> strings.booking_details_done_explanation.desc()
            Status.CANCELLED -> strings.booking_details_cancelled_explanation.desc()
            Status.MISSED -> strings.booking_details_missed_explanation.desc()
        }
    }
}
