import shared

@MainActor
class IOSEmployeesStateFactory: @MainActor EmployeesStateFactory {
	func createEmployeeListState() -> any EmployeeListState {
		return IOSEmployeeListState()
	}

	func createInviteEmployeeState() -> any InviteEmployeeState {
		return IOSInviteEmployeeState()
	}
}
