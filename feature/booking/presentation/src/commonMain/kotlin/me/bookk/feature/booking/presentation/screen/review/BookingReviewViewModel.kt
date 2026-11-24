package me.bookk.feature.booking.presentation.screen.review

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.booking.domain.api.GetBookingPreparation
import me.bookk.feature.booking.domain.api.GetBookingTimetable
import me.bookk.feature.booking.domain.api.GetBusinessMessage
import me.bookk.feature.booking.domain.api.SendBookingRequest
import me.bookk.feature.booking.domain.api.entity.BookingPreparation
import me.bookk.feature.booking.domain.api.entity.BookingTimetable
import me.bookk.feature.booking.presentation.BookingStateFactory
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class BookingReviewViewModel(
    @InjectedParam private val businessId: Uuid,
    private val getBookingTimetable: GetBookingTimetable,
    private val getBookingPreparation: GetBookingPreparation,
    private val getBusinessMessage: GetBusinessMessage,
    private val sendBookingRequest: SendBookingRequest,
    dateLocalizer: DateLocalizer,
    stateFactory: BookingStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    private val summaries = BookingReviewSummaries(dateLocalizer)

    private var timetable: BookingTimetable? = null

    val uiState: BookingReviewState = stateFactory.createBookingReviewState().setup()

    init {
        observeBooking()
        refreshBusinessMessage()
    }

    private fun BookingReviewState.setup() = apply {
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(BookingReviewDestinations.Back)
        }
        note.label = BookingRes.strings.booking_review_note_label.desc()
        note.placeholder = BookingRes.strings.booking_review_note_placeholder.desc()
        note.maxLength = NOTE_MAX_LENGTH
        note.onTextChanged = weakVMClosure { viewModel, text: String ->
            viewModel.uiState.note.text = text
        }
        sendButton.text = BookingRes.strings.booking_review_send.desc()
        sendButton.onClick = weakVMClosure { it.sendRequest() }
    }

    private fun observeBooking() {
        combine(
            getBookingTimetable.flow(businessId),
            getBookingPreparation.flow(businessId),
            getBusinessMessage.flow(businessId),
            ::Triple,
        )
            .flowOn(DispatcherProvider.io)
            .safeOnEach { (timetable, preparation, message) ->
                this.timetable = timetable
                uiState.businessName = timetable.businessName.desc()
                renderBusinessMessage(timetable, message)
                renderBooking(timetable, preparation)
            }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun refreshBusinessMessage() {
        launch(
            key = REFRESH_MESSAGE_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            call = { getBusinessMessage.refresh(businessId) },
            onError = { showMessageRefreshError(it) },
        )
    }

    private fun showMessageRefreshError(error: Throwable) {
        when (error) {
            is GetBusinessMessage.Error.BookingUnavailable -> leaveUnavailableBooking()
            else -> handleError(error)
        }
    }

    private fun leaveUnavailableBooking() {
        uiState.notifications.add(
            PresentationNotification.Message(
                message = BookingRes.strings.booking_date_time_unavailable.format(businessName()),
                buttons = emptyList(),
            ),
        )
        uiState.navigation.push(BookingReviewDestinations.Leave)
    }

    private fun renderBusinessMessage(timetable: BookingTimetable, message: String?) {
        uiState.businessMessage = message?.let {
            BusinessMessage(
                title = BookingRes.strings.booking_review_business_message_title
                    .format(timetable.businessName),
                text = it.desc(),
            )
        }
    }

    private fun renderBooking(timetable: BookingTimetable, preparation: BookingPreparation) {
        val summary = summaries.summary(timetable, preparation) ?: return
        uiState.summary = summary
        uiState.confirmationNotice = BookingRes.strings.booking_review_confirmation_notice
            .format(timetable.businessName)
    }

    private fun sendRequest() {
        val note = uiState.note.text
        launch(
            key = SEND_REQUEST_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = { uiState.sendButton.startLoading() },
            call = { sendBookingRequest(businessId, note) },
            onComplete = {
                uiState.notifications.add(PresentationNotification.SuccessHaptic)
                uiState.navigation.push(BookingReviewDestinations.Sent(businessId))
            },
            onError = { showSendError(it) },
            onTerminate = { uiState.sendButton.stopLoading() },
        )
    }

    private fun showSendError(error: Throwable) {
        val strings = BookingRes.strings
        when (error) {
            is SendBookingRequest.Error.SelectionIncomplete -> {
                uiState.navigation.push(BookingReviewDestinations.Leave)
            }

            is SendBookingRequest.Error.TimeUnavailable -> {
                showMessage(
                    strings.booking_review_time_unavailable_title.desc(),
                    strings.booking_review_time_unavailable_message.desc(),
                )
            }

            is SendBookingRequest.Error.PendingRequestsLimitReached -> {
                showMessage(
                    strings.booking_review_requests_limit_title.desc(),
                    strings.booking_review_requests_limit_message.format(businessName()),
                )
            }

            is SendBookingRequest.Error.PricesChanged -> {
                showMessage(
                    strings.booking_review_prices_changed_title.desc(),
                    strings.booking_review_prices_changed_message.desc(),
                )
            }

            is SendBookingRequest.Error.ServicesChanged -> {
                showMessage(
                    strings.booking_review_services_changed_title.desc(),
                    strings.booking_review_services_changed_message.format(businessName()),
                )
            }

            is SendBookingRequest.Error.SpecialistUnavailable -> {
                showMessage(
                    strings.booking_review_specialist_unavailable_title.desc(),
                    strings.booking_review_specialist_unavailable_message.desc(),
                )
            }

            else -> {
                uiState.notifications.add(error.notification())
            }
        }
    }

    private fun showMessage(title: StringDesc, message: StringDesc) {
        uiState.notifications.add(
            PresentationNotification.Message(
                title = title,
                message = message,
                buttons = emptyList(),
            ),
        )
    }

    private fun businessName(): String {
        return timetable?.businessName.orEmpty()
    }

    companion object {
        const val NOTE_MAX_LENGTH = 2048
        private const val SEND_REQUEST_KEY = "sendRequest"
        private const val REFRESH_MESSAGE_KEY = "refreshMessage"
    }
}
