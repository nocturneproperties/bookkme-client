import shared

@MainActor
class IOSEmployeesStateFactory: @MainActor EmployeesStateFactory {
	func createEmployeeListState() -> any EmployeeListState {
		return IOSEmployeeListState()
	}

	func createInviteEmployeeState() -> any InviteEmployeeState {
		return IOSInviteEmployeeState()
	}

	func createEditEmployeeState() -> any EditEmployeeState {
		return IOSEditEmployeeState()
	}

	func createResourcePermissionState() -> any ResourcePermissionState {
		return IOSResourcePermissionState()
	}
}
