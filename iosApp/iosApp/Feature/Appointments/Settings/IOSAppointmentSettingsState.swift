import shared
import SwiftUI

@Observable
@MainActor
class IOSAppointmentSettingsState: @MainActor AppointmentSettingsState, NativeStateRepresentation {

    typealias SwiftType = IOSAppointmentSettingsState
    typealias KotlinType = AppointmentSettingsState

    let appBar: any AppBarState
	
	let automaticApproval: any BooleanState
	let schedule: [any DaySettingsState]
	let dayOffs: any MultiPickerState

    let save: any ButtonState

    let navigation: any NavigationState
    let notifications: any PresentationNotificationState

    init() {
        appBar = IOSAppBarState()
        save = IOSButtonState()
        navigation = IOSNavigationState()
        notifications = IOSNotificationState()
		schedule = []
		dayOffs = IOSMultiPickerState()
		automaticApproval = IOSBooleanState()
    }
}
