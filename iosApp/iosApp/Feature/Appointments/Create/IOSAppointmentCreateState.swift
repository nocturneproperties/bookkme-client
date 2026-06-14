import shared
import SwiftUI

@Observable
@MainActor
class IOSAppointmentCreateState: @MainActor AppointmentCreateState, NativeStateRepresentation {

    typealias SwiftType = IOSAppointmentCreateState
    typealias KotlinType = AppointmentCreateState

    let appBar: any AppBarState
	
	let clientPicker: any PickerFieldState
	let datePicker: any DatePickerFieldState
	let timePicker: any TimePickerFieldState
	let servicePickers: any ListState
	let note: any TextFieldState
	let create: any ButtonState
	
    let navigation: any NavigationState
    let notifications: any PresentationNotificationState

    init() {
        appBar = IOSAppBarState()
		clientPicker = IOSPickerState()
		datePicker = IOSDatePickerFieldState()
		timePicker = IOSTimePickerFieldState()
		servicePickers = IOSListState<IOSPickerState>()
		note = IOSTextFieldState()
		create = IOSButtonState()
        navigation = IOSNavigationState()
        notifications = IOSNotificationState()
    }
}
