//
//  IOSDateTimePickerState.swift
//  iosApp
//
//  Created by BookkMe on 17.06.2026.
//  Copyright © 2026 ValthSolutions. All rights reserved.
//

import shared
import SwiftUI

@MainActor
@Observable
class IOSDateTimePickerState: @MainActor DateTimePickerState {

    var isDatePickerVisible: Bool
    var pickedDate: LocalDateTime?
    var maxDate: LocalDateTime?
    var minDate: LocalDateTime?
    var onDatePicked: ((LocalDateTime) -> Void)?

    init(
        isDatePickerVisible: Bool = false,
        pickedDate: LocalDateTime? = nil,
        maxDate: LocalDateTime? = nil,
        minDate: LocalDateTime? = nil,
        onDatePicked: ((LocalDateTime) -> Void)? = nil
    ) {
        self.isDatePickerVisible = isDatePickerVisible
        self.pickedDate = pickedDate
        self.maxDate = maxDate
        self.minDate = minDate
        self.onDatePicked = onDatePicked
    }
}

extension DateTimePickerState {
    func impl() -> IOSDateTimePickerState {
        return self as! IOSDateTimePickerState
    }
}
