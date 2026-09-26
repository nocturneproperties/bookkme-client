package me.bookk.feature.appointments.presentation

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.test.FakeAppBarState
import me.bookk.designsystem.test.FakeBooleanState
import me.bookk.designsystem.test.FakeButtonState
import me.bookk.designsystem.test.FakeDatePickerFieldState
import me.bookk.designsystem.test.FakeDatePickerState
import me.bookk.designsystem.test.FakeDateTimePickerState
import me.bookk.designsystem.test.FakeListState
import me.bookk.designsystem.test.FakeNavigationState
import me.bookk.designsystem.test.FakeNotificationState
import me.bookk.designsystem.test.FakeOptionsMultiPickerState
import me.bookk.designsystem.test.FakePickerFieldState
import me.bookk.designsystem.test.FakeRefreshState
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.designsystem.test.FakeTimePickerFieldState
import me.bookk.designsystem.test.FakeViewState
import me.bookk.designsystem.uistate.SimplePickerPresentation
import me.bookk.designsystem.uistate.simple.InfoLine
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.presentation.screen.create.AppointmentCreateDestination
import me.bookk.feature.appointments.presentation.screen.create.AppointmentCreateState
import me.bookk.feature.appointments.presentation.screen.create.ServicePickerPresentation
import me.bookk.feature.appointments.presentation.screen.details.AppointmentDetailsDestination
import me.bookk.feature.appointments.presentation.screen.details.AppointmentDetailsState
import me.bookk.feature.appointments.presentation.screen.details.UIAppointmentStatus
import me.bookk.feature.appointments.presentation.screen.history.AppointmentHistoryDestinations
import me.bookk.feature.appointments.presentation.screen.history.AppointmentHistoryItemState
import me.bookk.feature.appointments.presentation.screen.history.AppointmentHistoryState
import me.bookk.feature.appointments.presentation.screen.list.AppointmentItemState
import me.bookk.feature.appointments.presentation.screen.list.AppointmentListDestinations
import me.bookk.feature.appointments.presentation.screen.list.AppointmentListState
import me.bookk.feature.appointments.presentation.screen.list.DateInfo
import me.bookk.feature.appointments.presentation.screen.request.AppointmentRequestItemState
import me.bookk.feature.appointments.presentation.screen.request.AppointmentRequestState
import me.bookk.feature.appointments.presentation.screen.settings.AppointmentSettingsDestination
import me.bookk.feature.appointments.presentation.screen.settings.AppointmentSettingsState

internal class FakeAppointmentsStateFactory : AppointmentsStateFactory {
    override fun createAppointmentListState(): AppointmentListState = FakeAppointmentListState()
    override fun createAppointmentCreateState(): AppointmentCreateState = FakeAppointmentCreateState()
    override fun createAppointmentDetailsState(): AppointmentDetailsState = FakeAppointmentDetailsState()
    override fun createAppointmentSettingsState(): AppointmentSettingsState = FakeAppointmentSettingsState()
    override fun createAppointmentHistoryState(): AppointmentHistoryState = FakeAppointmentHistoryState()
    override fun createAppointmentRequestState(): AppointmentRequestState = FakeAppointmentRequestState()
}

internal class FakeAppointmentListState : AppointmentListState {
    override val appBar = FakeAppBarState()
    override val requestsButton = FakeButtonState()
    override val datePicker = FakeDatePickerState()
    override val dates = FakeListState<DateInfo>()
    override val appointments = FakeListState<AppointmentItemState>()
    override val refresh = FakeRefreshState()
    override var isRequestsVisible: Boolean = false
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<AppointmentListDestinations>()
}

internal class FakeAppointmentCreateState : AppointmentCreateState {
    override val appBar = FakeAppBarState()
    override val clientPicker = FakePickerFieldState<SimplePickerPresentation<ClientSnapshot>>()
    override val servicePicker = FakeOptionsMultiPickerState<ServicePickerPresentation>()
    override var subtotalLabel: StringDesc = "".desc()
    override var subtotalPrice: String = ""
    override val datePicker = FakeDatePickerFieldState()
    override val timePicker = FakeTimePickerFieldState()
    override val note = FakeTextFieldState()
    override val create = FakeButtonState()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<AppointmentCreateDestination>()
}

internal class FakeAppointmentDetailsState : AppointmentDetailsState {
    override val appBar = FakeAppBarState()
    override var status: UIAppointmentStatus = UIAppointmentStatus("".desc(), ColorToken.ActionText)
    override val dateTimePicker = FakeDateTimePickerState()
    override val rescheduleButton = FakeButtonState()
    override val infoSections = FakeListState<InfoLine>()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<AppointmentDetailsDestination>()
}

internal class FakeAppointmentSettingsState : AppointmentSettingsState {
    override val appBar = FakeAppBarState()
    override val automaticApproval = FakeBooleanState()
    override val note = FakeTextFieldState()
    override val minimalBreak = FakeTextFieldState()
    override val save = FakeButtonState()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<AppointmentSettingsDestination>()
}

internal class FakeAppointmentHistoryState : AppointmentHistoryState {
    override val appBar = FakeAppBarState()
    override val searchField = FakeTextFieldState()
    override val appointments = FakeListState<AppointmentHistoryItemState>()
    override val refresh = FakeRefreshState()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<AppointmentHistoryDestinations>()
}

internal class FakeAppointmentRequestState : AppointmentRequestState {
    override val requests = FakeListState<AppointmentRequestItemState>()
    override val notifications = FakeNotificationState()

    override fun createAppointmentRequestItemState(): AppointmentRequestItemState {
        return FakeAppointmentRequestItemState()
    }
}

internal class FakeAppointmentRequestItemState : FakeViewState(), AppointmentRequestItemState {
    override var clientName: String = ""
    override var serviceName: String = ""
    override var scheduledDate: String = ""
    override var scheduledTime: String = ""
    override var note: String = ""
    override var earnings: String = ""
    override val approveButton = FakeButtonState()
    override val declineButton = FakeButtonState()
}
