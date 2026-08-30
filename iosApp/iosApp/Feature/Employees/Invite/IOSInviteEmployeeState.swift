import SwiftUI
import shared

@Observable
@MainActor
class IOSInviteEmployeeState: @MainActor InviteEmployeeState, NativeStateRepresentation {
	typealias SwiftType = IOSInviteEmployeeState
	typealias KotlinType = InviteEmployeeState

	var appBar: any AppBarState
	var navigation: any NavigationState
	var notifications: any PresentationNotificationState

	init() {
		self.appBar = IOSAppBarState()
		self.navigation = IOSNavigationState()
		self.notifications = IOSNotificationState()
	}
}
