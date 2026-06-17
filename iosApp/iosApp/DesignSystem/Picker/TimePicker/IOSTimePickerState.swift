//
//  IOSTimePickerState.swift
//  iosApp
//
//  Created by BookkMe on 06.02.2026.
//  Copyright © 2026 ValthSolutions. All rights reserved.
//

import shared
import SwiftUI

@MainActor
@Observable
class IOSTimePickerState: @MainActor TimePickerState {

    var isTimePickerVisible: Bool
    var maxTime: LocalTime?
    var minTime: LocalTime?
    var pickedTime: LocalTime?
    var onTimePicked: ((LocalTime) -> Void)?

    init(
        isTimePickerVisible: Bool = false,
        maxTime: LocalTime? = nil,
        minTime: LocalTime? = nil,
        pickedTime: LocalTime? = nil,
        onTimePicked: ((LocalTime) -> Void)? = nil
    ) {
        self.isTimePickerVisible = isTimePickerVisible
        self.maxTime = maxTime
        self.minTime = minTime
        self.pickedTime = pickedTime
        self.onTimePicked = onTimePicked
    }
}

extension TimePickerState {
    func impl() -> IOSTimePickerState {
        return self as! IOSTimePickerState
    }
}
