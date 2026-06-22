//
//  DatePickerField.swift
//  iosApp
//
//

import SwiftUI
import UIKit
import shared

struct DatePickerField: View {
    @Bindable private var state: IOSDatePickerFieldState

    init(state: DatePickerFieldState) {
        self._state = Bindable(wrappedValue: IOSDatePickerFieldState.cast(state))
    }

    var body: some View {
        if state.isVisible {
            StateTextField(state.textField)
                .contentShape(Rectangle())
                .simultaneousGesture(TapGesture().onEnded {
                    guard state.textField.enabled else { return }
                    dismissKeyboard()
                    state.datePicker.isDatePickerVisible = true
                })
                .sheet(isPresented: Binding(
                    get: { state.datePicker.isDatePickerVisible },
                    set: { state.datePicker.isDatePickerVisible = $0 }
                )) {
                    AppDatePicker(state: state.datePicker)
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

#Preview("Date Picker Field") {
    @Previewable
    @State
    var value: IOSDatePickerFieldState = IOSDatePickerFieldState(
        textField: IOSTextFieldState(
			placeholder: RawStringDesc(string: "Date of birth")
		)
    )

    VStack(spacing: 16) {
        DatePickerField(state: value)
    }
    .padding(16)
    .background(AppColors.background)
}
