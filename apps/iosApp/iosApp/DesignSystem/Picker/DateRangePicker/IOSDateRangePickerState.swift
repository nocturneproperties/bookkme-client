import shared
import SwiftUI

@MainActor
@Observable
final class IOSDateRangePickerState: IOSViewState, @MainActor DateRangePickerState, NativeStateRepresentation {

    typealias SwiftType = IOSDateRangePickerState
    typealias KotlinType = DateRangePickerState

    var title: any StringDesc
    let startDate: any DatePickerFieldState
    let endDate: any DatePickerFieldState
    var onDateRangeSelected: () -> Void

    init(
        title: any StringDesc = RawStringDesc(string: ""),
        startDate: (any DatePickerFieldState)? = nil,
        endDate: (any DatePickerFieldState)? = nil,
        onDateRangeSelected: @escaping () -> Void = {}
    ) {
        self.title = title
        self.startDate = startDate ?? IOSDatePickerFieldState()
        self.endDate = endDate ?? IOSDatePickerFieldState()
        self.onDateRangeSelected = onDateRangeSelected
        super.init()
    }
}
