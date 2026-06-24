import shared
import SwiftUI

@Observable
@MainActor
class IOSAppointmentSettingsState: @MainActor AppointmentSettingsState, NativeStateRepresentation {

    typealias SwiftType = IOSAppointmentSettingsState
    typealias KotlinType = AppointmentSettingsState

    let appBar: any AppBarState

    let automaticApproval: any BooleanState
    let dayOffs: any MultiPickerState
    let dateRange: any DateRangePickerState
    let schedule: any ScheduleState
    let note: any TextFieldState
    let minimalBreak: any TextFieldState

    let save: any ButtonState

    let navigation: any NavigationState
    let notifications: any PresentationNotificationState

    init() {
        appBar = IOSAppBarState()
        automaticApproval = IOSBooleanState()
        dayOffs = IOSMultiPickerState()
        dateRange = IOSDateRangePickerState()
        schedule = IOSScheduleState()
        note = IOSTextFieldState()
        minimalBreak = IOSTextFieldState()
        save = IOSButtonState()
        navigation = IOSNavigationState()
        notifications = IOSNotificationState()
    }

    func createDaySettingState() -> any DaySettingsState {
        return IOSDaySettingsState()
    }
}
