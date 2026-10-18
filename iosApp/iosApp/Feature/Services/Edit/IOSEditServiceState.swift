import shared

@Observable
@MainActor
class IOSEditServiceState: @MainActor EditServiceState, NativeStateRepresentation {

	typealias SwiftType = IOSEditServiceState
	typealias KotlinType = EditServiceState

	var appBar: any AppBarState

	var navigation: any NavigationState
	var notifications: any PresentationNotificationState

	init() {
		appBar = IOSAppBarState()
		navigation = IOSNavigationState()
		notifications = IOSNotificationState()
	}
}
