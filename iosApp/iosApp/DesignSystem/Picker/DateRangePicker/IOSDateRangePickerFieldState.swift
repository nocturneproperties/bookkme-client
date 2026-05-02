//
//  IOSDateRangePickerState.swift
//  iosApp
//

import shared

@MainActor
class IOSDateRangePickerFieldState: IOSViewState, @MainActor DateRangePickerFieldState {
    
    var textField: any TextFieldState
    var startDate: LocalDate?
    var endDate: LocalDate?
    var hasPeriodSelected: Bool
    var onClick: (() -> Void)?
    
    init(
        endDate: LocalDate? = nil,
        startDate: LocalDate? = nil,
        hasPeriodSelected: Bool = false,
        textField: (any TextFieldState)? = nil,
        isVisible: Bool = true,
        onClick: (() -> Void)? = nil,
    ) {
        self.endDate = endDate
        self.hasPeriodSelected = hasPeriodSelected
        self.onClick = onClick
        self.startDate = startDate
		self.textField = textField ?? IOSTextFieldState()
        super.init(isVisible: isVisible)
    }
    
}
