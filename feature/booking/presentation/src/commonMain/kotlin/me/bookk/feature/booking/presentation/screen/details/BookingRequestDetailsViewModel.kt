package me.bookk.feature.booking.presentation.screen.details

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.flow.flowOn
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.booking.domain.api.GetBookingRequestDetails
import me.bookk.feature.booking.domain.api.WithdrawBookingRequest
import me.bookk.feature.booking.domain.api.entity.BookingRequestDetails
import me.bookk.feature.booking.domain.api.entity.BookingRequestDetails.Status
import me.bookk.feature.booking.presentation.BookingStateFactory
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class BookingRequestDetailsViewModel(
    @InjectedParam private val requestId: Uuid,
    private val getBookingRequestDetails: GetBookingRequestDetails,
    private val withdrawBookingRequest: WithdrawBookingRequest,
    dateLocalizer: DateLocalizer,
    stateFactory: BookingStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    private val items = BookingDetailsItems(dateLocalizer)

    private var businessName = ""

    private var hasLeft = false

    val uiState: BookingDetailsState = stateFactory.createBookingDetailsState().setup()

    init {
        observeRequest()
    }

    private fun BookingDetailsState.setup() = apply {
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(BookingDetailsDestinations.Back)
        }
        actionButton.text = BookingRes.strings.booking_details_withdraw.desc()
        actionButton.isVisible = false
        actionButton.onClick = weakVMClosure { it.confirmWithdrawal() }
    }

    private fun observeRequest() {
        getBookingRequestDetails.flow(requestId)
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderRequest(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun renderRequest(details: BookingRequestDetails?) {
        if (details == null) {
            uiState.actionButton.isVisible = false
            leave()
            return
        }
        businessName = details.summary.businessName
        uiState.booking = items.item(details.summary, details.status.presentation(businessName))
        uiState.actionButton.isVisible = details.status == Status.WAITING_FOR_CONFIRMATION
    }

    private fun leave() {
        if (hasLeft) return
        hasLeft = true
        uiState.navigation.push(BookingDetailsDestinations.Back)
    }

    private fun Status.presentation(businessName: String): BookingStatusPresentation {
        val strings = BookingRes.strings
        return when (this) {
            Status.WAITING_FOR_CONFIRMATION -> BookingStatusPresentation(
                strings.booking_sent_status_waiting.desc(),
                ColorToken.SecondaryText,
                strings.booking_details_waiting_explanation.format(businessName),
            )

            Status.CONFIRMED -> BookingStatusPresentation(
                strings.booking_sent_status_confirmed.desc(),
                ColorToken.Success,
                strings.booking_details_confirmed_explanation.format(businessName),
            )

            Status.DECLINED -> BookingStatusPresentation(
                strings.booking_details_status_declined.desc(),
                ColorToken.Error,
                strings.booking_details_declined_explanation.format(businessName),
            )

            Status.CLOSED -> BookingStatusPresentation(
                strings.booking_details_status_closed.desc(),
                ColorToken.SecondaryText,
                strings.booking_details_closed_explanation.desc(),
            )
        }
    }

    private fun confirmWithdrawal() {
        val strings = BookingRes.strings
        uiState.notifications.add(
            PresentationNotification.Message(
                title = strings.booking_details_withdraw_title.desc(),
                message = strings.booking_details_withdraw_message.format(businessName),
                buttons = listOf(
                    ButtonDescriptor(
                        text = strings.booking_details_withdraw_keep.desc(),
                        actionType = ActionType.CANCEL,
                    ),
                    ButtonDescriptor(
                        text = strings.booking_details_withdraw_confirm.desc(),
                        actionType = ActionType.NEGATIVE,
                        onClick = weakVMClosure { it.withdraw() },
                    ),
                ),
            ),
        )
    }

    private fun withdraw() {
        launch(
            key = WITHDRAW_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = { uiState.actionButton.startLoading() },
            call = { withdrawBookingRequest(requestId) },
            onComplete = {
                uiState.notifications.add(PresentationNotification.SuccessHaptic)
                uiState.navigation.push(BookingDetailsDestinations.Bookings)
            },
            onError = { showWithdrawError(it) },
            onTerminate = { uiState.actionButton.stopLoading() },
        )
    }

    private fun showWithdrawError(error: Throwable) {
        val strings = BookingRes.strings
        when (error) {
            is WithdrawBookingRequest.Error.AlreadyConfirmed -> {
                leaveWithMessage(
                    strings.booking_details_already_confirmed_title.desc(),
                    strings.booking_details_already_confirmed_message.format(businessName),
                )
            }

            is WithdrawBookingRequest.Error.NoLongerPending -> {
                leaveWithMessage(
                    strings.booking_details_no_longer_pending_title.desc(),
                    strings.booking_details_no_longer_pending_message.desc(),
                )
            }

            else -> {
                uiState.notifications.add(error.notification())
            }
        }
    }

    private fun leaveWithMessage(title: StringDesc, message: StringDesc) {
        uiState.notifications.add(
            PresentationNotification.Message(
                title = title,
                message = message,
                buttons = emptyList(),
            ),
        )
        leave()
    }

    private companion object {
        const val WITHDRAW_KEY = "withdraw"
    }
}
