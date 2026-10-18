import shared

@Observable
@MainActor
class IOSEditServiceState: @MainActor EditServiceState, NativeStateRepresentation {

	typealias SwiftType = IOSEditServiceState
	typealias KotlinType = EditServiceState

	var appBar: any AppBarState

	var group: any PickerFieldState
	var name: any TextFieldState
	var duration: any TextFieldState
	var price: any TextFieldState
	var available: any BooleanState

	var submit: any ButtonState
	var deleteButton: any ButtonState

	var navigation: any NavigationState
	var notifications: any PresentationNotificationState

	init() {
		appBar = IOSAppBarState()
		group = IOSPickerState()
		name = IOSTextFieldState()
		duration = IOSTextFieldState()
		price = IOSTextFieldState()
		available = IOSBooleanState()
		submit = IOSButtonState()
		deleteButton = IOSButtonState()
		navigation = IOSNavigationState()
		notifications = IOSNotificationState()
	}
}
