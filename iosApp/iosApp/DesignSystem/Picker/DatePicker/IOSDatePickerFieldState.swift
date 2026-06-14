//
//  IOSDatePickerState.swift
//  iosApp
//
//  Created by BookkMe on 06.02.2026.
//  Copyright © 2026 ValthSolutions. All rights reserved.
//

import shared
import SwiftUI
import Observation

@MainActor
@Observable
final class IOSDatePickerFieldState: IOSViewState, @MainActor DatePickerFieldState, NativeStateRepresentation {

    typealias SwiftType = IOSDatePickerFieldState
    typealias KotlinType = DatePickerFieldState

    var textField: any TextFieldState
    var maxDate: LocalDate?
    var minDate: LocalDate?
    var onDatePicked: ((LocalDate) -> Void)?
    var pickedDate: LocalDate?

    init(
        textField: (any TextFieldState)? = nil,
        maxDate: LocalDate? = nil,
        minDate: LocalDate? = nil,
        onDatePicked: ((LocalDate) -> Void)? = nil,
        pickedDate: LocalDate? = nil
    ) {
        self.textField = textField ?? IOSTextFieldState(readOnly: true)
        self.maxDate = maxDate
        self.minDate = minDate
        self.onDatePicked = onDatePicked
        self.pickedDate = pickedDate
    }
}
