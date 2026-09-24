import SwiftUI
import shared

struct ScheduleSections: View {

	let state: any ScheduleState

	var body: some View {
		let schedule = IOSScheduleState.cast(state)
		let scheduleDays = schedule.list.items(DaySettingsState.self)
		Section(DesignSystem.strings().schedule_title.desc().localized()) {
			ScheduleStrip(schedule: scheduleDays)
				.listRowInsets(EdgeInsets())
			if let expanded = scheduleDays.first(where: { $0.isVisible }) {
				ScheduleDay(state: expanded)
					.listRowInsets(EdgeInsets())
			}
		}
		.listRowSeparator(.hidden)

		Section(schedule.dayOffs.pickerTitle.localized()) {
			MultiPicker(
				schedule.dayOffs,
				pickerContent: {
					DateRangePicker(state: schedule.dateRange) {
						schedule.dayOffs.isPickerVisible = false
					}
				},
				itemContent: { item, onRemove in
					DayOffItem(item: item, onDeleteClick: onRemove)
				}
			)
		}
	}
}

private struct DayOffItem: View {
    let item: PickerPresentation
    let onDeleteClick: () -> Void

    var body: some View {
		HStack(alignment: .center, spacing: 8) {
			Image(systemName: "calendar")
				.foregroundStyle(AppColors.primary)
			Text(item.displayName.localized())
				.font(.headline)
				.frame(maxWidth: .infinity, alignment: .leading)
			Button(action: onDeleteClick) {
				Image(systemName: "trash")
					.foregroundStyle(AppColors.error)
			}
			.buttonStyle(.plain)
		}
    }
}

private struct ScheduleStrip: View {

    let schedule: [DaySettingsState]

    var body: some View {
		HStack(spacing: 0) {
			ForEach(schedule, id: \.id) { day in
				DayOfWeekCell(state: day) {
					withAnimation {
						for d in schedule {
							d.isVisible = d.id == day.id ? !d.isVisible : false
						}
					}
				}
			}
		}
		.padding(10)
    }
}

private struct DayOfWeekCell: View {
    let state: any DaySettingsState
    let onClick: () -> Void

    var body: some View {
        Button(action: onClick) {
            Text(state.dayIndicator.localized())
                .font(.headline)
                .frame(width: 40, height: 40)
				.foregroundStyle(state.isActive.isChecked ? AppColors.onAction : AppColors.primary)
                .background(state.isActive.isChecked ? AppColors.buttonPrimary : Color.clear, in: Circle())
                .overlay(
                    Circle()
						.stroke(AppColors.primary, lineWidth: state.isVisible ? 2 : 0)
                )
        }
        .buttonStyle(.plain)
        .frame(maxWidth: .infinity)
    }
}

private struct ScheduleDay: View {
    let state: any DaySettingsState

    var body: some View {
		HStack(spacing: 8) {
			Text(state.dayIndicator.localized())
				.font(.headline)
				.frame(width: 40, height: 40)
				.foregroundStyle(AppColors.onAction)
				.background(state.isActive.isChecked ? AppColors.actionText : AppColors.inactive, in: Circle())
			Text(state.title.localized())
				.font(.headline)
				.frame(maxWidth: .infinity, alignment: .leading)
			StateSwitch(state: state.isActive) { newValue in
				state.isActive.onCheckedChange?(KotlinBoolean(bool: newValue))
			}
		}
		.padding(16)

		ForEach(state.intervals, id: \.id) { interval in
			TimeRow(timeSettings: interval) {
				withAnimation {
					state.onDeleteInterval(interval)
				}
			}
			.padding(.horizontal, 16)
			.padding(.bottom, 8)
		}

		TextButton(state.addTimeButton)
    }
}

private struct TimeRow: View {
    let timeSettings: any TimeSettingState
    let onDeleteClick: () -> Void

    var body: some View {
        HStack(spacing: 0) {
			TimePickerField(state: timeSettings.timeFromPicker)
				.textFieldStyle(.onElevated)
            Text("-")
                .font(.subheadline)
                .foregroundStyle(AppColors.secondary)
                .padding(.horizontal, 8)
            TimePickerField(state: timeSettings.timeToPicker)
				.textFieldStyle(.onElevated)
            Button(action: onDeleteClick) {
                Image(systemName: "xmark")
                    .foregroundStyle(AppColors.secondary)
            }
            .buttonStyle(.plain)
            .padding(.leading, 16)
        }.background(AppColors.elevated)
    }
}
