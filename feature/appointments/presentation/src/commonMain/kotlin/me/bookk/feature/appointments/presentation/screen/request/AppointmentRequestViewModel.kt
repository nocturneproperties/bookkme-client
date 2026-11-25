package me.bookk.feature.appointments.presentation.screen.request

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import library.device.api.DeviceFacade
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.convenience.listPager
import me.bookk.designsystem.convenience.resetListOnChange
import me.bookk.designsystem.resources.toStringDesc
import me.bookk.designsystem.simple
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.appointments.domain.api.ApproveAppointmentRequest
import me.bookk.feature.appointments.domain.api.ApproveAppointmentRequest.Error
import me.bookk.feature.appointments.domain.api.DeclineAppointmentRequest
import me.bookk.feature.appointments.domain.api.GetAssignedAppointmentRequests
import me.bookk.feature.appointments.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import kotlin.time.Duration
import kotlin.uuid.Uuid

class AppointmentRequestViewModel(
    private val getAssignedAppointmentRequests: GetAssignedAppointmentRequests,
    private val approveAppointmentRequest: ApproveAppointmentRequest,
    private val declineAppointmentRequest: DeclineAppointmentRequest,
    private val observeCurrentBusinessId: ObserveCurrentBusinessId,
    private val device: DeviceFacade,
    stateFactory: AppointmentsStateFactory,
    dateLocalizer: DateLocalizer,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState: AppointmentRequestState = stateFactory.createAppointmentRequestState().setup()

    private val timeFormatter = dateLocalizer.forStyle(DateStyle.SHORT)
    private val dateFormatter = dateLocalizer.forStyle(DateStyle.D_MMM_YYYY_RELATIVE)

    private val requestPages = listPager(
        uiState.requests,
        uiState.notifications,
        getAssignedAppointmentRequests,
    )

    init {
        observeRequests()
        loadRequestsOfCurrentBusiness()
    }

    private fun AppointmentRequestState.setup() = apply {
        title = AppointmentsRes.strings.appointments_requests.desc()
        requests.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = AppointmentsRes.strings.appointments_request_empty.desc(),
        )
    }

    private fun observeRequests() {
        observeCurrentBusinessId()
            .filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { getAssignedAppointmentRequests.flow(it) }
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderRequests(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun loadRequestsOfCurrentBusiness() {
        observeCurrentBusinessId()
            .filterNotNull()
            .flowOn(DispatcherProvider.io)
            .resetListOnChange(uiState.requests)
            .safeOnEach { requestPages.load(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun renderRequests(requests: List<AppointmentRequest>) {
        if (requests.isEmpty() && uiState.requests.isInitialLoading) return
        uiState.requests.replace(requests.map(::createRequestItemState))
    }

    private suspend fun currentBusinessId(): Uuid {
        return observeCurrentBusinessId().filterNotNull().first()
    }

    private fun createRequestItemState(request: AppointmentRequest): AppointmentRequestItemState {
        return uiState.createAppointmentRequestItemState().apply {
            id = request.id.toString()
            clientName = request.client.fullName
            clientInitials = initialsOf(request.client.fullName)
            schedule = scheduleOf(request)
            services = request.services.map(::serviceLabelOf)
            price = request.total
            note = request.note
            onCallClick = request.client.phone
                ?.takeIf { it.isNotBlank() }
                ?.let { phone -> weakVMClosure { it.device.dial(phone) } }
            approveButton.text = DesignSystem.strings.action_approve.desc()
            approveButton.onClick = weakVMClosure { it.onApprove(request.id) }
            declineButton.text = DesignSystem.strings.action_decline.desc()
            declineButton.onClick = weakVMClosure { it.onDeclineRequested(request.id) }
        }
    }

    private fun initialsOf(fullName: String): String {
        val words = fullName.split(" ").filter { it.isNotBlank() }
        return listOfNotNull(words.firstOrNull(), words.drop(1).lastOrNull())
            .map { it.first().uppercaseChar() }
            .joinToString("")
    }

    private fun scheduleOf(request: AppointmentRequest): StringDesc {
        val timeZone = TimeZone.currentSystemDefault()
        val start = request.date.toLocalDateTime(timeZone)
        val totalDuration = request.services.fold(Duration.ZERO) { acc, it -> acc + it.duration }
        val end = (request.date + totalDuration).toLocalDateTime(timeZone)
        return AppointmentsRes.strings.appointments_request_schedule.format(
            dateFormatter.format(start.date, relative = true),
            timeFormatter.format(start.time),
            timeFormatter.format(end.time),
        )
    }

    private fun serviceLabelOf(service: ServiceSnapshot): StringDesc {
        return AppointmentsRes.strings.appointments_request_service.format(
            service.name,
            service.duration.toStringDesc(),
        )
    }

    private fun onApprove(requestId: Uuid) {
        val item = uiState.requests.items.find { it.id == requestId.toString() } ?: return
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { item.approveButton.startLoading() },
            call = { approveAppointmentRequest(requestId) },
            onComplete = { removeHandledRequest(item) },
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

                    is Error.EmployeeDateNotAllowed -> {
                        AppointmentsRes.strings.appointments_employee_date_error.desc()
                    }

                    is Error.EmployeeTimeNotAllowed -> {
                        AppointmentsRes.strings.appointments_employee_time_error.desc()
                    }

                    is Error.EmployeeSuspended -> {
                        AppointmentsRes.strings.appointments_details_employee_suspended_error.desc()
                    }

                    is Error.EmployeeNotFound -> {
                        AppointmentsRes.strings.appointments_employee_not_found_error.desc()
                    }

                    null -> {
                        null
                    }
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
            call = { declineAppointmentRequest(requestId, currentBusinessId(), reason) },
            onComplete = { removeHandledRequest(item) },
            onError = { uiState.notifications.add(it.notification()) },
            onTerminate = { item.declineButton.stopLoading() },
        )
    }

    private fun removeHandledRequest(item: AppointmentRequestItemState) {
        uiState.notifications.add(PresentationNotification.SuccessHaptic)
        uiState.requests.replace(uiState.requests.items - item)
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
                onConfirm = weakVMClosure { vm, reason -> vm.onDecline(requestId, reason) },
            ),
        )
    }
}
