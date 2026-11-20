//
//  DateTimePicker.swift
//  iosApp
//

import SwiftUI
import shared

struct DateTimePicker: View {
    @Bindable private var state: IOSDateTimePickerState

    init(_ state: any DateTimePickerState) {
        let impl = state.impl()
        self._state = Bindable(wrappedValue: impl)
    }

    var body: some View {
		DateTimePickerSheet(
			selection: state.pickedDate ?? localDateTimeTime(from: Date()),
			minDate: state.minDate,
			maxDate: state.maxDate,
			onDismiss: { state.isDatePickerVisible = false },
			onDatePicked: {
				state.onDatePicked?($0)
				state.isDatePickerVisible = false
			}
		)
    }
}

struct DateTimePickerSheet: View {
	
	let selectedDate: LocalDateTime
	let minDate: LocalDateTime?
	let maxDate: LocalDateTime?
	let onDismiss: () -> Void
	let onDatePicked: (LocalDateTime) -> Void
	
	private var range: ClosedRange<Date> {
		( dateFromLocalDateTime(minDate) ?? Date.distantPast)...(dateFromLocalDateTime(maxDate) ?? Date.distantFuture)
	}
	
	@State private var selection: Date
	
	init(
		selection: LocalDateTime,
		minDate: LocalDateTime?,
		maxDate: LocalDateTime?,
		onDismiss: @escaping () -> Void,
		onDatePicked: @escaping (LocalDateTime) -> Void
	) {
		self.selectedDate = selection
		self.onDismiss = onDismiss
		self.onDatePicked = onDatePicked
		self.minDate = minDate
		self.maxDate = maxDate
		self._selection = State(initialValue: dateFromLocalDateTime(selection) ?? Date())
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
					onDatePicked(localDateTimeTime(from: selection))
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
					in: range
				)
				.datePickerStyle(.graphical)
				.tint(AppColors.actionText)
				Spacer()
			}
		}
		.presentationBackground(AppColors.elevated)
		.presentationDetents([.medium])
	}
}

private func dateFromLocalDateTime(_ localDateTime: LocalDateTime?) -> Date? {
	guard let localDateTime else { return nil }
	var components = DateComponents()
	components.year = Int(localDateTime.date.year)
	components.month = Int(localDateTime.date.month.number)
	components.day = Int(localDateTime.date.day)
	components.hour = Int(localDateTime.time.hour)
	components.minute = Int(localDateTime.time.minute)
	return Calendar.current.date(from: components)
}

private func localDateTimeTime(from date: Date) -> LocalDateTime {
	let components = Calendar.current.dateComponents([.year, .month, .day, .hour, .minute], from: date)
	let hour = Int32(components.hour ?? 0)
	let min = Int32(components.minute ?? 0)
	let year = Int32(components.year ?? 1970)
	let day = Int32(components.day ?? 1)
	let monthValue = Int32(components.month ?? 1)
	
	return LocalDateTime(year: year, month: monthValue, day: day, hour: hour, minute: min, second: 0, nanosecond: 0)
}
