import shared
import SwiftUI

@MainActor
@Observable
final class IOSTimeSettingState: IOSViewState, @MainActor TimeSettingState {

    var timeFromPicker: any TimePickerFieldState
    var timeToPicker: any TimePickerFieldState
    var isDeleteAvailable: Bool

    init(isDeleteAvailable: Bool = true) {
        timeFromPicker = IOSTimePickerFieldState()
        timeToPicker = IOSTimePickerFieldState()
        self.isDeleteAvailable = isDeleteAvailable
        super.init()
    }
}

extension shared.TimeSettingState {
    func impl() -> IOSTimeSettingState {
        return self as! IOSTimeSettingState
    }
}
