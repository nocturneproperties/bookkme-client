import SwiftUI
import shared

struct AppointmentSettingsScreen: View {

    @EnvironmentObject var navigationStack: NavigationStackHolder
    @StateViewModel var viewModel: AppointmentSettingsViewModel

    init(businessId: KotlinUuid) {
        _viewModel = StateViewModel(
            wrappedValue: IosAppointmentsPresentationDiKt.appointmentSettingsVM(businessId: businessId)
        )
    }

    var body: some View {
        let state = IOSAppointmentSettingsState.cast(viewModel.uiState)
        ScrollView {
			VStack(alignment: .leading, spacing: 16) {
                ScheduleStrip(schedule: state.schedule)

                MultiPicker(
                    state.dayOffs,
                    pickerContent: {
                        DateRangePicker(state: state.dateRange) {
                            state.dayOffs.isPickerVisible = false
                        }
                    },
                    itemContent: { item, onRemove in
                        DayOffItem(item: item, onDeleteClick: onRemove)
                    }
                )

                RequestsSettings(state: state)
				Header(text: AppointmentsRes.strings().appointments_settings_note_header.desc().localized())
				StateTextField(state.note, textEditor: true)
					.lineLimit(3...5)

                StateButton(state.save)
                    .padding(.top, 16)
            }
            .padding(16)
        }
        .withNavigationBar(state.appBar)
        .sendLifecycleEventsTo(viewModel)
        .handleNotifications(state.notifications)
        .handleNavigation(state.navigation) { destination in
            switch destination {
            case is AppointmentSettingsDestination.Back:
                navigationStack.popLast()
            default:
                break
            }
        }
    }
}

private struct DayOffItem: View {
    let item: PickerPresentation
    let onDeleteClick: () -> Void

    var body: some View {
		VStack(spacing: 0) {
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
			.padding()
			Divider()
		}
    }
}

private struct RequestsSettings: View {
    let state: IOSAppointmentSettingsState

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            VStack(alignment: .leading, spacing: 8) {
                Header(text: AppointmentsRes.strings().appointments_settings_requests.desc().localized())
                StateSwitch(state: state.automaticApproval) { newValue in
                    state.automaticApproval.onCheckedChange?(KotlinBoolean(bool: newValue))
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .background(AppColors.elevated, in: RoundedRectangle(cornerRadius: 12))
            }
            StateTextField(state.minimalBreak)
        }
    }
}

private struct ScheduleStrip: View {
    let schedule: IOSScheduleState

    init(schedule: any ScheduleState) {
        self.schedule = IOSScheduleState.cast(schedule)
    }

    var body: some View {
        let days = schedule.asList()
        VStack(alignment: .leading, spacing: 8) {
            Header(text: AppointmentsRes.strings().appointments_settings_schedule.desc().localized())
            HStack(spacing: 0) {
                ForEach(days, id: \.id) { day in
                    DayOfWeekCell(state: day) {
                        withAnimation {
                            for d in days {
                                d.isVisible = d.id == day.id ? !d.isVisible : false
                            }
                        }
                    }
                }
            }
			Text(AppointmentsRes.strings().appointments_settings_schedule_hint.desc().localized())
				.foregroundStyle(AppColors.secondary)
				.font(.subheadline)
				.padding(.leading)
            if let expanded = days.first(where: { $0.isVisible }) {
                ScheduleDay(state: expanded)
                    .transition(.opacity)
            }
        }
        .animation(.default, value: days.first(where: { $0.isVisible })?.id)
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
        VStack(alignment: .leading, spacing: 0) {
            HStack(spacing: 8) {
                Text(state.dayIndicator.localized())
                    .frame(width: 40, height: 40)
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

            TextButton(state.addTimeButton, textAlignment: .leading)
        }
        .background(AppColors.elevated, in: RoundedRectangle(cornerRadius: 12))
        .padding(.top, 8)
        .animation(.easeInOut, value: state.intervals.count)
    }
}

private struct TimeRow: View {
    let timeSettings: any TimeSettingState
    let onDeleteClick: () -> Void

    var body: some View {
        HStack(spacing: 0) {
			TimePickerField(state: timeSettings.timeFromPicker, fieldColor: AppColors.background)
            Text("-")
                .font(.subheadline)
                .foregroundStyle(AppColors.secondary)
                .padding(.horizontal, 8)
            TimePickerField(state: timeSettings.timeToPicker, fieldColor: AppColors.background)
            Button(action: onDeleteClick) {
                Image(systemName: "xmark")
                    .foregroundStyle(AppColors.secondary)
            }
            .buttonStyle(.plain)
            .padding(.leading, 16)
        }
    }
}
