import shared
import SwiftUI

@MainActor
@Observable
final class IOSDaySettingsState: IOSViewState, @MainActor DaySettingsState, NativeStateRepresentation {

    typealias SwiftType = IOSDaySettingsState
    typealias KotlinType = DaySettingsState

    var isActive: any BooleanState
    var dayIndicator: any StringDesc
    var title: any StringDesc
    var intervals: [any TimeSettingState]
    let addTimeButton: any ButtonState
    var onDeleteInterval: (any TimeSettingState) -> Void

    init() {
        isActive = IOSBooleanState()
        dayIndicator = RawStringDesc(string: "")
        title = RawStringDesc(string: "")
        intervals = []
        addTimeButton = IOSButtonState()
        onDeleteInterval = { _ in }
        super.init(isVisible: false)
    }

    func replaceIntervals(newIntervals: [any TimeSettingState]) {
        intervals = newIntervals
    }

    func createTimeSettingState() -> any TimeSettingState {
        return IOSTimeSettingState()
    }
}

extension shared.DaySettingsState {
    func impl() -> IOSDaySettingsState {
        return self as! IOSDaySettingsState
    }
}
