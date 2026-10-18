import SwiftUI
import shared

@Observable
@MainActor
class IOSAppointmentCompletionState: @MainActor AppointmentCompletionState, NativeStateRepresentation {

	typealias SwiftType = IOSAppointmentCompletionState
	typealias KotlinType = AppointmentCompletionState

	var isSheetVisible: Bool
	var title: any StringDesc
	let servicePicker: any OptionsMultiPickerState
	var servicesHint: any StringDesc
	let price: any TextFieldState
	let note: any TextFieldState
	let confirmButton: any ButtonState

	init() {
		isSheetVisible = false
		title = RawStringDesc(string: "")
		servicePicker = IOSOptionsMultiPickerState()
		servicesHint = RawStringDesc(string: "")
		price = IOSTextFieldState()
		note = IOSTextFieldState()
		confirmButton = IOSButtonState()
	}
}
