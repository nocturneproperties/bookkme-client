import shared
import SwiftUI

@MainActor
@Observable
final class IOSDaySettingsState: @MainActor DaySettingsState {
	var isExpanded: Bool
    var isActive: Bool
    var title: any StringDesc
    var intervals: [TimeSettingState]
    var addTimeButton: any ButtonState
    var onDeleteInterval: () -> Void

    init(title: any StringDesc, isActive: Bool = false) {
        self.title = title
        self.isActive = isActive
        self.intervals = []
        self.addTimeButton = IOSButtonState()
        self.onDeleteInterval = {}
		self.isExpanded = false
    }

    func replaceIntervals(newIntervals: [TimeSettingState]) {
        intervals = newIntervals
    }
}

extension shared.DaySettingsState {
    func impl() -> IOSDaySettingsState {
        return self as! IOSDaySettingsState
    }
}
