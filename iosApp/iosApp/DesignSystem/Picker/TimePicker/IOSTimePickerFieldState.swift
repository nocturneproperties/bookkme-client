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
    var onTimePicked: ((LocalTime) -> Void)?
    var pickedTime: LocalTime?

    init(
        textField: (any TextFieldState)? = nil,
        onTimePicked: ((LocalTime) -> Void)? = nil,
		pickedTime: LocalTime? = nil
    ) {
        self.textField = textField ?? IOSTextFieldState()
        self.onTimePicked = onTimePicked
        self.pickedTime = pickedTime
    }
}
