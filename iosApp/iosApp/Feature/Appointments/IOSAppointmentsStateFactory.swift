import shared

@MainActor
class IOSAppointmentsStateFactory: @MainActor AppointmentsStateFactory {

    func createAppointmentListState() -> any AppointmentListState {
        return IOSAppointmentListState()
    }

    func createAppointmentCreateState() -> any AppointmentCreateState {
        return IOSAppointmentCreateState()
    }
	
	func createServicePickerItemState() -> any PickerFieldState {
		return IOSPickerState()
	}
}
