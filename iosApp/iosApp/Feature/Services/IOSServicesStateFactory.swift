import shared

@MainActor
class IOSServicesStateFactory: @MainActor ServicesStateFactory {
	
	func createServiceListState() -> any ServiceListState {
		return IOSServiceListState()
	}
	
	func createServiceGroupListState() -> any ServiceGroupListState {
		return IOSServiceGroupListState()
	}
	
	func createServiceState() -> any AddServiceState {
		return IOSAddServiceState()
	}
	
	func createAddGroupState() -> any AddGroupState {
		return IOSAddGroupState()
	}
}
