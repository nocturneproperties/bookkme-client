import shared
import SwiftUI

@Observable
@MainActor
class IOSAppointmentCreateState: @MainActor AppointmentCreateState, NativeStateRepresentation {

    typealias SwiftType = IOSAppointmentCreateState
    typealias KotlinType = AppointmentCreateState

    let appBar: any AppBarState

    let clientPicker: any PickerFieldState
    let servicePicker: any MultiPickerState
    var subtotalLabel: any StringDesc
    var subtotalPrice: String
    let datePicker: any DatePickerFieldState
    let timePicker: any TimePickerFieldState
    let note: any TextFieldState
    let create: any ButtonState

    let navigation: any NavigationState
    let notifications: any PresentationNotificationState

    init() {
        appBar = IOSAppBarState()
        clientPicker = IOSPickerState()
        servicePicker = IOSMultiPickerState()
        subtotalLabel = RawStringDesc(string: "")
        subtotalPrice = ""
        datePicker = IOSDatePickerFieldState()
        timePicker = IOSTimePickerFieldState()
        note = IOSTextFieldState()
        create = IOSButtonState()
        navigation = IOSNavigationState()
        notifications = IOSNotificationState()
    }
}
