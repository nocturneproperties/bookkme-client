import shared
import SwiftUI

@Observable
@MainActor
class IOSServiceGroupListState: @MainActor ServiceGroupListState, NativeStateRepresentation {
	
	typealias SwiftType = IOSServiceGroupListState
	typealias KotlinType = ServiceGroupListState
	
	var appBar: any AppBarState
	
	var groups: any ListState
	var refreshState: any RefreshState
	var search: any TextFieldState
	var isAddGroupDialogVisible: Bool
	var isAddGroupDialogVisibleBinding: Binding<Bool> {
		Binding(
			get: { self.isAddGroupDialogVisible },
			set: { self.isAddGroupDialogVisible = $0 }
		)
	}

	
	var navigation: any NavigationState
	var notifications: any PresentationNotificationState
	
	init() {
		appBar = IOSAppBarState()
		navigation = IOSNavigationState()
		notifications = IOSNotificationState()
		groups = IOSListState<ServiceGroupListStateServiceGroupUI>()
		refreshState = IOSRefreshState()
		search = IOSTextFieldState()
		isAddGroupDialogVisible = false
	}
}

extension ServiceGroupListStateServiceGroupUI:@retroactive Identifiable {}
