//
//  IOSDatePickerState.swift
//  iosApp
//
//  Created by BookkMe on 06.02.2026.
//  Copyright © 2026 ValthSolutions. All rights reserved.
//

import shared
import SwiftUI

@MainActor
@Observable
class IOSDatePickerState: @MainActor DatePickerState {

    var isDatePickerVisible: Bool
    var pickedDate: LocalDate?
    var maxDate: LocalDate?
    var minDate: LocalDate?
    var onDatePicked: ((LocalDate) -> Void)?

    init(
        isDatePickerVisible: Bool = false,
        pickedDate: LocalDate? = nil,
        maxDate: LocalDate? = nil,
        minDate: LocalDate? = nil,
        onDatePicked: ((LocalDate) -> Void)? = nil
    ) {
        self.isDatePickerVisible = isDatePickerVisible
        self.pickedDate = pickedDate
        self.maxDate = maxDate
        self.minDate = minDate
        self.onDatePicked = onDatePicked
    }
}

extension DatePickerState {
    func impl() -> IOSDatePickerState {
        return self as! IOSDatePickerState
    }
}
