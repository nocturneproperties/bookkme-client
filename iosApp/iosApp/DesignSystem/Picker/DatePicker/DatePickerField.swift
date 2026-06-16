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
    private let onDatePicked: (LocalDate) -> Void

    @State private var isPresented = false

    init(state: DatePickerFieldState, onDatePicked: ((LocalDate) -> Void)? = nil) {
		self._state = Bindable(wrappedValue: IOSDatePickerFieldState.cast(state))
        self.onDatePicked = onDatePicked ?? { date in
			state.onDatePicked?(date)
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
					DatePickerSheet(
						selection: state.pickedDate ?? localDate(from: Date()),
                        minDate: dateFromLocal(state.minDate),
                        maxDate: dateFromLocal(state.maxDate),
						onDismiss: {
							isPresented = false
						},
						onDatePicked: {
                            onDatePicked($0)
                            isPresented = false
                        }
                    )
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

struct DatePickerSheet: View {
	
	let selectedDate: LocalDate
	let minDate: Date?
	let maxDate: Date?
	let onDismiss: () -> Void
	let onDatePicked: (LocalDate) -> Void
	
	@State private var selection: Date
	
	init(
		selection: LocalDate,
		minDate: Date? = nil,
		maxDate: Date? = nil,
		onDismiss: @escaping () -> Void,
		onDatePicked: @escaping (LocalDate) -> Void
	) {
		self.selectedDate = selection
		self.minDate = minDate
		self.maxDate = maxDate
		self.onDismiss = onDismiss
		self.onDatePicked = onDatePicked
		self._selection = State(initialValue: dateFromLocal(selection) ?? Date())
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
					onDatePicked(localDate(from: selection))
				}) {
					Text(DesignSystem.strings.shared.action_select.desc().localized())
						.font(.body)
						.foregroundStyle(AppColors.actionText)
				}
				.padding(.trailing, 16)
			}
			.padding(.vertical, 12)
			
			DatePicker(
				"",
				selection: $selection,
				in: (minDate ?? Date.distantPast)...(maxDate ?? Date.distantFuture),
				displayedComponents: [.date]
			)
				.datePickerStyle(.graphical)
				.tint(AppColors.actionText)
				.padding(.horizontal, 8)
		}
		.presentationBackground(AppColors.elevated)
		.presentationDetents([.medium])
	}
}

private func dateFromLocal(_ localDate: LocalDate?) -> Date? {
	guard let localDate else { return nil }
	var components = DateComponents()
	components.year = Int(localDate.year)
	components.month = Int(localDate.month.number)
	components.day = Int(localDate.day)
	return Calendar.current.date(from: components)
}

private func localDate(from date: Date) -> LocalDate {
	let components = Calendar.current.dateComponents([.year, .month, .day], from: date)
	let year = Int32(components.year ?? 1970)
	let day = Int32(components.day ?? 1)
	let monthValue = Int32(components.month ?? 1)
	return LocalDate(year: year, month: monthValue, day: day)
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
