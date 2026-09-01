import SwiftUI
import shared

@Observable
@MainActor
class IOSInviteEmployeeState: @MainActor InviteEmployeeState, NativeStateRepresentation {
	typealias SwiftType = IOSInviteEmployeeState
	typealias KotlinType = InviteEmployeeState

	var appBar: any AppBarState
	var descriptionText: any StringDesc
	var emailField: any TextFieldState
	var sendButton: any ButtonState
	var invitationsHeader: any StringDesc
	var invitationsList: any ListState
	var refreshState: any RefreshState
	var navigation: any NavigationState
	var notifications: any PresentationNotificationState

	init() {
		self.appBar = IOSAppBarState()
		self.descriptionText = RawStringDesc(string: "")
		self.emailField = IOSTextFieldState()
		self.sendButton = IOSButtonState()
		self.invitationsHeader = RawStringDesc(string: "")
		self.invitationsList = IOSListState<InvitationItem>()
		self.refreshState = IOSRefreshState()
		self.navigation = IOSNavigationState()
		self.notifications = IOSNotificationState()
	}
}

extension InvitationItem: @retroactive Identifiable {}
