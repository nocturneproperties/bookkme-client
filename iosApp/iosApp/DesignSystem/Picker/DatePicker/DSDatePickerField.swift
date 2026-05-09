//
//  DSDatePickerField.swift
//  iosApp
//
//

import SwiftUI
import UIKit
import shared

struct DSDatePickerField: View {
    @Bindable private var state: IOSDatePickerState
    private let onDatePicked: (LocalDate) -> Void

    @State private var isPresented = false
    @State private var selection = Date()

    init(state: DatePickerState, onDatePicked: ((LocalDate) -> Void)? = nil) {
        let impl = state.impl()
        self.state = impl
        self.onDatePicked = onDatePicked ?? { date in
            impl.onDatePicked?(date)
        }
    }

    var body: some View {
        if state.isVisible {
            StateTextField(state.textField)
                .contentShape(Rectangle())
                .simultaneousGesture(TapGesture().onEnded {
                    guard state.textField.enabled else { return }
                    dismissKeyboard()
                    selection = dateFromLocal(state.pickedDate) ?? Date()
                    isPresented = true
                })
                .sheet(isPresented: $isPresented) {
                    DatePickerSheet(
                        selection: $selection,
                        minDate: dateFromLocal(state.minDate),
                        maxDate: dateFromLocal(state.maxDate),
                        onSelect: {
                            onDatePicked(localDate(from: selection))
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

    private func dateFromLocal(_ localDate: LocalDate?) -> Date? {
        guard let localDate else { return nil }
        var components = DateComponents()
        components.year = Int(localDate.year)
        components.month = monthIndex(from: localDate.month)
        components.day = Int(localDate.day)
        return Calendar.current.date(from: components)
    }

    private func localDate(from date: Date) -> LocalDate {
        let components = Calendar.current.dateComponents([.year, .month, .day], from: date)
        let year = Int32(components.year ?? 1970)
        let day = Int32(components.day ?? 1)
        let monthValue = Int32(components.month ?? 1)
        let entries = Month.entries
        let index = max(0, min(Int(monthValue - 1), entries.count - 1))
        let month = entries.isEmpty ? Month.january : entries[index]
        return LocalDate(year: year, month: month, day_: day)
    }

    private func monthIndex(from month: Month) -> Int {
        return Int(month.ordinal) + 1
    }
}

private struct DatePickerSheet: View {
    @Binding var selection: Date
    let minDate: Date?
    let maxDate: Date?
    let onSelect: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Spacer()
                Button(action: { onSelect() }) {
                    Text(DesignSystem.strings.shared.action_done.desc().localized())
						.font(.body)
                        .foregroundStyle(AppColors.actionText)
                }
                .padding(.trailing, 16)
                .padding(.top, 12)
                .padding(.bottom, 8)
            }

            DatePicker(
                "",
                selection: $selection,
                in: (minDate ?? Date.distantPast)...(maxDate ?? Date.distantFuture),
                displayedComponents: [.date]
            )
            .datePickerStyle(.wheel)
            .labelsHidden()
            .frame(maxWidth: .infinity)
            .clipped()
        }
    }
}

#Preview("Date Picker Field") {
    @Previewable
    @State
    var value: IOSDatePickerState = IOSDatePickerState(
        textField: IOSTextFieldState(
			placeholder: RawStringDesc(string: "Date of birth")
		)
    )

    VStack(spacing: 16) {
        DSDatePickerField(state: value) { date in
            value.pickedDate = date
        }
    }
    .padding(16)
    .background(AppColors.background)
}
