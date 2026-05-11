import shared

@MainActor
class IOSServicesStateFactory: @MainActor ServicesStateFactory {
	func createServiceListState() -> any ServiceListState {
		return IOSServiceListState()
	}
}
