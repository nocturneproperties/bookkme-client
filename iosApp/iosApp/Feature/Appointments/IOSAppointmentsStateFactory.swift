import shared

@MainActor
class IOSAppointmentsStateFactory: @MainActor AppointmentsStateFactory {

    func createAppointmentListState() -> any AppointmentListState {
        return IOSAppointmentListState()
    }

    func createAppointmentCreateState() -> any AppointmentCreateState {
        return IOSAppointmentCreateState()
    }

    func createAppointmentDetailsState() -> any AppointmentDetailsState {
        return IOSAppointmentDetailsState()
    }

    func createAppointmentSettingsState() -> any AppointmentSettingsState {
        return IOSAppointmentSettingsState()
    }

    func createAppointmentHistoryState() -> any AppointmentHistoryState {
        return IOSAppointmentHistoryState()
    }

    func createAppointmentRequestState() -> any AppointmentRequestState {
        return IOSAppointmentRequestState()
    }

    func createAppointmentRequestItemState() -> any AppointmentRequestItemState {
        return IOSAppointmentRequestItemState()
    }

	func createServicePickerItemState() -> any PickerFieldState {
		return IOSPickerState()
	}
}
