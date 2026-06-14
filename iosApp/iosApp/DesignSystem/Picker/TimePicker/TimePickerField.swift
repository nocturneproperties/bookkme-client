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
    private let onTimePicked: (LocalTime) -> Void

    @State private var isPresented = false

    init(state: TimePickerFieldState, onTimePicked: ((LocalTime) -> Void)? = nil) {
		self._state = Bindable(wrappedValue: IOSTimePickerFieldState.cast(state))
        self.onTimePicked = onTimePicked ?? { date in
			state.onTimePicked?(date)
        }
    }

    var body: some View {
        if state.isVisible {
            StateTextField(state.textField)
                .contentShape(Rectangle())
                .simultaneousGesture(TapGesture().onEnded {
                    guard state.textField.enabled else { return }
                    dismissKeyboard()
                    isPresented = true
                })
                .sheet(isPresented: $isPresented) {
					TimePickerSheet(
						selection: state.pickedTime ?? localTime(from: Date()),
						onDismiss: {
							isPresented = false
						},
						onTimePicked: {
                            onTimePicked($0)
                            isPresented = false
                        }
                    )
                    .presentationDetents([.height(300)])
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

struct TimePickerSheet: View {
	
	let selectedDTime: LocalTime
	let onDismiss: () -> Void
	let onTimePicked: (LocalTime) -> Void
	
	@State private var selection: Date
	
	init(
		selection: LocalTime,
		onDismiss: @escaping () -> Void,
		onTimePicked: @escaping (LocalTime) -> Void
	) {
		self.selectedDTime = selection
		self.onDismiss = onDismiss
		self.onTimePicked = onTimePicked
		self._selection = State(initialValue: timeFromLocal(selection) ?? Date())
	}
	
	var body: some View {
		VStack(spacing: 0) {
			HStack {
				Button(action: onDismiss) {
					Text(DesignSystem.strings.shared.action_cancel.desc().localized())
						.font(.body)
						.foregroundStyle(AppColors.actionText)
				}
				.padding(.leading, 16)
				Spacer()
				Button(action: {
					onTimePicked(localTime(from: selection))
				}) {
					Text(DesignSystem.strings.shared.action_select.desc().localized())
						.font(.body)
						.foregroundStyle(AppColors.actionText)
				}
				.padding(.trailing, 16)
			}
			.padding(.vertical, 12)
			HStack {
				Spacer()
				DatePicker(
					"",
					selection: $selection,
					displayedComponents: [.hourAndMinute]
				)
				.labelsHidden()
				.datePickerStyle(.wheel)
				.tint(AppColors.actionText)
				Spacer()
			}
		}
		.presentationBackground(AppColors.elevated)
		.presentationDetents([.medium])
	}
}

private func timeFromLocal(_ localTime: LocalTime?) -> Date? {
	guard let localTime else { return nil }
	var components = DateComponents()
	components.hour = Int(localTime.hour)
	components.minute = Int(localTime.minute)
	return Calendar.current.date(from: components)
}

private func localTime(from date: Date) -> LocalTime {
	let components = Calendar.current.dateComponents([.hour, .minute], from: date)
	let hour = Int32(components.hour ?? 0)
	let min = Int32(components.minute ?? 0)
	return LocalTime(hour: hour, minute: min, second: 0, nanosecond: 0)
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
        DatePickerField(state: value) { date in
            value.pickedDate = date
        }
    }
    .padding(16)
    .background(AppColors.background)
}
