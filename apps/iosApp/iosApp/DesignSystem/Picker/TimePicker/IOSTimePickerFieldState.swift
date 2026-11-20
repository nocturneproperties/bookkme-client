//
//  IOSTimePickerFieldState.swift
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
final class IOSTimePickerFieldState: IOSViewState, @MainActor TimePickerFieldState, NativeStateRepresentation {

    typealias SwiftType = IOSTimePickerFieldState
    typealias KotlinType = TimePickerFieldState

    var textField: any TextFieldState
    var timePicker: any TimePickerState

    init(
        textField: (any TextFieldState)? = nil,
        timePicker: (any TimePickerState)? = nil
    ) {
        self.textField = textField ?? IOSTextFieldState(readOnly: true)
        self.timePicker = timePicker ?? IOSTimePickerState()
    }
}
