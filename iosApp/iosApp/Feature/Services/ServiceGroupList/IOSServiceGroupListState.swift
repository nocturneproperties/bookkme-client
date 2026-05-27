import shared

@Observable
@MainActor
class IOSServiceGroupListState: @MainActor ServiceGroupListState, NativeStateRepresentation {
	
	typealias SwiftType = IOSServiceGroupListState
	typealias KotlinType = ServiceGroupListState
	
	var appBar: any AppBarState
	var navigation: any NavigationState
	var notifications: any PresentationNotificationState
	
	init() {
		appBar = IOSAppBarState()
		navigation = IOSNavigationState()
		notifications = IOSNotificationState()
	}
}
