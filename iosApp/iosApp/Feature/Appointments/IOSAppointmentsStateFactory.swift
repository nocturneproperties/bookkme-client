import shared

@MainActor
class IOSAppointmentsStateFactory: @MainActor AppointmentsStateFactory {

    func createAppointmentListState() -> any AppointmentListState {
        return IOSAppointmentListState()
    }
}
