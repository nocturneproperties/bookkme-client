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
    var datePicker: any DatePickerState

    init(
        textField: (any TextFieldState)? = nil,
        datePicker: (any DatePickerState)? = nil
    ) {
        self.textField = textField ?? IOSTextFieldState(readOnly: true)
        self.datePicker = datePicker ?? IOSDatePickerState()
    }
}
