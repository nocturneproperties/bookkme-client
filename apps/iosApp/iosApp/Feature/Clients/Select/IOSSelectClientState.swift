import SwiftUI
import shared

@Observable
@MainActor
class IOSSelectClientState: @MainActor SelectClientState, NativeStateRepresentation {
	typealias SwiftType = IOSSelectClientState

	typealias KotlinType = SelectClientState

	var appBar: any AppBarState

	var searchField: any TextFieldState

	var clients: any ListState

	var refreshState: any RefreshState

	var navigation: any NavigationState

	var notifications: any PresentationNotificationState

	init() {
		self.appBar = IOSAppBarState()
		self.searchField = IOSTextFieldState()
		self.clients = IOSListState<ClientListItem>()
		self.refreshState = IOSRefreshState()
		self.navigation = IOSNavigationState()
		self.notifications = IOSNotificationState()
	}
}
