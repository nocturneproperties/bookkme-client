import shared

@Observable
@MainActor
class IOSServiceGroupListState: @MainActor ServiceGroupListState, NativeStateRepresentation {
	
	
	typealias SwiftType = IOSServiceGroupListState
	typealias KotlinType = ServiceGroupListState
	
	var appBar: any AppBarState
	
	var groups: any ListState
	var refreshState: any RefreshState
	var search: any TextFieldState
	
	var navigation: any NavigationState
	var notifications: any PresentationNotificationState
	
	init() {
		appBar = IOSAppBarState()
		navigation = IOSNavigationState()
		notifications = IOSNotificationState()
		groups = IOSListState<ServiceGroupListStateServiceGroupUI>()
		refreshState = IOSRefreshState()
		search = IOSTextFieldState()
	}
}

extension ServiceGroupListStateServiceGroupUI:@retroactive Identifiable {}
