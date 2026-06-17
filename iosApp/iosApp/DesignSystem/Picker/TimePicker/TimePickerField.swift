//
//  TimePickerField.swift
//  iosApp
//
//

import SwiftUI
import UIKit
import shared

struct TimePickerField: View {
    @Bindable private var state: IOSTimePickerFieldState

    init(state: TimePickerFieldState) {
        self._state = Bindable(wrappedValue: IOSTimePickerFieldState.cast(state))
    }

    var body: some View {
        if state.isVisible {
            StateTextField(state.textField)
                .contentShape(Rectangle())
                .simultaneousGesture(TapGesture().onEnded {
                    guard state.textField.enabled else { return }
                    dismissKeyboard()
                    state.timePicker.isTimePickerVisible = true
                })
                .sheet(isPresented: Binding(
                    get: { state.timePicker.isTimePickerVisible },
                    set: { state.timePicker.isTimePickerVisible = $0 }
                )) {
                    AppTimePicker(state: state.timePicker)
                }
        }
    }

    private func dismissKeyboard() {
        UIApplication.shared.sendAction(
            #selector(UIResponder.resignFirstResponder),
            to: nil,
            from: nil,
            for: nil
        )
    }
}

#Preview("Time Picker Field") {
    @Previewable
    @State
    var value: IOSTimePickerFieldState = IOSTimePickerFieldState(
        textField: IOSTextFieldState(
			placeholder: RawStringDesc(string: "Time")
		)
    )

    VStack(spacing: 16) {
        TimePickerField(state: value)
    }
    .padding(16)
    .background(AppColors.background)
}
