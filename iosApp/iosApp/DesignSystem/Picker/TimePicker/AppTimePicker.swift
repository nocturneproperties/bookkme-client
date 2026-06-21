//
//  AppTimePicker.swift
//  iosApp
//

import SwiftUI
import shared

struct AppTimePicker: View {
    @Bindable private var state: IOSTimePickerState

    init(state: any TimePickerState) {
        self._state = Bindable(wrappedValue: state.impl())
    }

    var body: some View {
        TimePickerSheet(
            selection: state.pickedTime ?? localTime(from: Date()),
            minTime: timeFromLocal(state.minTime),
            maxTime: timeFromLocal(state.maxTime),
            onDismiss: { state.isTimePickerVisible = false },
            onTimePicked: { time in
                state.onTimePicked?(time)
                state.isTimePickerVisible = false
            }
        )
    }
}

struct TimePickerSheet: View {

	let selectedTime: LocalTime
	let minTime: Date?
	let maxTime: Date?
	let onDismiss: () -> Void
	let onTimePicked: (LocalTime) -> Void

	@State private var selection: Date

	private var range: ClosedRange<Date> {
		(minTime ?? Date.distantPast)...(maxTime ?? Date.distantFuture)
	}

	init(
		selection: LocalTime,
		minTime: Date? = nil,
		maxTime: Date? = nil,
		onDismiss: @escaping () -> Void,
		onTimePicked: @escaping (LocalTime) -> Void
	) {
		self.selectedTime = selection
		self.minTime = minTime
		self.maxTime = maxTime
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
			Spacer()
			HStack {
				Spacer()
				DatePicker(
					"",
					selection: $selection,
					in: range,
					displayedComponents: [.hourAndMinute]
				)
				.labelsHidden()
				.datePickerStyle(.wheel)
				.tint(AppColors.actionText)
				Spacer()
			}
			Spacer()
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
