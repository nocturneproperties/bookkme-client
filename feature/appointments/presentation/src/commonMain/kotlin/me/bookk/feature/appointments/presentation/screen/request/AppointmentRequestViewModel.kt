package me.bookk.feature.appointments.presentation.screen.request

import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.simple
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.appointments.domain.api.ApproveAppointmentRequest
import me.bookk.feature.appointments.domain.api.ApproveAppointmentRequest.Error
import me.bookk.feature.appointments.domain.api.DeclineAppointmentRequest
import me.bookk.feature.appointments.domain.api.GetAppointmentRequests
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class AppointmentRequestViewModel(
    @InjectedParam private val businessId: Uuid,
    private val getAppointmentRequests: GetAppointmentRequests,
    private val approveAppointmentRequest: ApproveAppointmentRequest,
    private val declineAppointmentRequest: DeclineAppointmentRequest,
    stateFactory: AppointmentsStateFactory,
    dateLocalizer: DateLocalizer,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: AppointmentRequestState = stateFactory.createAppointmentRequestState().setup()

    private val timeFormatter = dateLocalizer.forStyle(DateStyle.SHORT)
    private val dateFormatter = dateLocalizer.forStyle(DateStyle.D_MMM_YYYY_RELATIVE)

    override fun onViewPresented() {
        super.onViewPresented()
        loadRequests()
    }

    private fun AppointmentRequestState.setup() = apply {
        requests.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = AppointmentsRes.strings.appointments_request_empty.desc(),
        )
    }

    private fun loadRequests() {
        launchCached(
            launchIn = DispatcherProvider.io,
            call = { getAppointmentRequests.cached(businessId, it) },
            onComplete = { uiState.requests.replace(it.map(::createRequestItemState)) },
            onError = { uiState.notifications.add(it.notification()) },
        )
    }

    private fun createRequestItemState(request: AppointmentRequest) =
        uiState.createAppointmentRequestItemState().apply {
            val localDateTime = request.date.toLocalDateTime(TimeZone.currentSystemDefault())
            id = request.id.toString()
            clientName = request.client.fullName
            serviceName = request.services.joinToString("\n") {
                "${it.name} · ${it.duration.inWholeMinutes} min"
            }
            scheduledDate = dateFormatter.format(localDateTime.date, relative = true)
            scheduledTime = timeFormatter.format(localDateTime.time)
            note = request.note
            earnings = request.total
            approveButton.text = DesignSystem.strings.action_approve.desc()
            approveButton.onClick = weakVMClosure { it.onApprove(request.id) }
            declineButton.text = DesignSystem.strings.action_decline.desc()
            declineButton.onClick = weakVMClosure { it.onDeclineRequested(request.id) }
        }

    private fun onApprove(requestId: Uuid) {
        val item = uiState.requests.items.find { it.id == requestId.toString() } ?: return
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { item.approveButton.startLoading() },
            call = { approveAppointmentRequest(requestId) },
            onComplete = { uiState.requests.replace(uiState.requests.items - item) },
            onError = {
                val resource = when (it as? Error) {
                    is Error.AppointmentExists -> {
                        AppointmentsRes.strings.appointments_request_overlap_error.desc()
                    }
                    is Error.DateInPast -> {
                        AppointmentsRes.strings.appointments_request_in_past.desc()
                    }
                    is Error.DateNotAllowed -> {
                        AppointmentsRes.strings.appointments_request_date_error.desc()
                    }
                    is Error.TimeNotAllowed -> {
                        AppointmentsRes.strings.appointments_request_time_error.desc()
                    }
                    null -> null
                }
                if (resource != null) {
                    uiState.notifications.add(PresentationNotification.Message.simple(resource))
                } else {
                    uiState.notifications.add(it.notification())
                }
            },
            onTerminate = { item.approveButton.stopLoading() },
        )
    }

    private fun onDecline(requestId: Uuid, reason: String) {
        val item = uiState.requests.items.find { it.id == requestId.toString() } ?: return
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { item.declineButton.startLoading() },
            call = { declineAppointmentRequest(requestId, businessId, reason) },
            onComplete = { uiState.requests.replace(uiState.requests.items - item) },
            onError = { uiState.notifications.add(it.notification()) },
            onTerminate = { item.declineButton.stopLoading() },
        )
    }

    private fun onDeclineRequested(requestId: Uuid) {
        uiState.notifications.add(
            PresentationNotification.InputMessage(
                title = DesignSystem.strings.action_confirm.desc(),
                message = AppointmentsRes.strings.appointments_request_decline.desc(),
                placeholder = AppointmentsRes.strings.appointments_details_cancel_reason.desc(),
                cancelText = DesignSystem.strings.action_ignore.desc(),
                confirmText = DesignSystem.strings.action_confirm.desc(),
                confirmActionType = ActionType.NEGATIVE,
                onConfirm = weakVMClosure { vm, reason -> vm.onDecline(requestId, reason) }
            )
        )
    }

}
