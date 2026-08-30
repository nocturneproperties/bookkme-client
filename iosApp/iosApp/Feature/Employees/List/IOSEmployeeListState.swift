import SwiftUI
import shared

@Observable
@MainActor
class IOSEmployeeListState: @MainActor EmployeeListState, NativeStateRepresentation {
	typealias SwiftType = IOSEmployeeListState
	typealias KotlinType = EmployeeListState

	var appBar: any AppBarState
	var employeesList: any ListState
	var navigation: any NavigationState
	var notifications: any PresentationNotificationState
	var refreshState: any RefreshState
	var searchField: any TextFieldState

	init() {
		self.appBar = IOSAppBarState()
		self.employeesList = IOSListState<EmployeeSection>()
		self.navigation = IOSNavigationState()
		self.notifications = IOSNotificationState()
		self.refreshState = IOSRefreshState()
		self.searchField = IOSTextFieldState()
	}
}

extension EmployeeSection: @retroactive Identifiable {}
