import SwiftUI
import shared

@Observable
@MainActor
class IOSEditEmployeeState: @MainActor EditEmployeeState, NativeStateRepresentation {
	typealias SwiftType = IOSEditEmployeeState
	typealias KotlinType = EditEmployeeState

	let appBar: any AppBarState
	let save: any ButtonState
	let contacts: any ListState
	let services: any OptionsMultiPickerState
	let schedule: any ScheduleState
	let suspension: any ButtonState
	let permissions: any ListState
	var isPermissionsVisible: Bool
	var permissionsHint: (any StringDesc)?
	let navigation: any NavigationState
	let notifications: any PresentationNotificationState

	init() {
		appBar = IOSAppBarState()
		save = IOSButtonState()
		contacts = IOSListState<InfoLine>()
		services = IOSOptionsMultiPickerState()
		schedule = IOSScheduleState()
		suspension = IOSButtonState(isVisible: false)
		permissions = IOSListState<ResourcePermissionState>()
		isPermissionsVisible = false
		permissionsHint = nil
		navigation = IOSNavigationState()
		notifications = IOSNotificationState()
	}
}

@Observable
@MainActor
final class IOSResourcePermissionState: IOSViewState, @MainActor ResourcePermissionState, NativeStateRepresentation {
	typealias SwiftType = IOSResourcePermissionState
	typealias KotlinType = ResourcePermissionState

	var title: any StringDesc
	let canView: any BooleanState
	let canUpdate: any BooleanState
	let canDelete: any BooleanState

	init() {
		title = RawStringDesc(string: "")
		canView = IOSBooleanState()
		canUpdate = IOSBooleanState()
		canDelete = IOSBooleanState()
		super.init()
	}
}
