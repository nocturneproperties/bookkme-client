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
		List {
			Section(AppointmentsRes.strings().appointments_settings_schedule.desc().localized()) {
				ScheduleStrip(schedule: state.schedule)
				if let expanded = IOSScheduleState.cast(state.schedule).asList().first(where: { $0.isVisible }) {
					ScheduleDay(state: expanded)
				}
			}
			.listRowInsets(EdgeInsets())
			.listRowSeparator(.hidden)
			Section(state.dayOffs.pickerTitle.localized()) {
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
				.listRowInsets(EdgeInsets())
			}
			Section {
				RequestsSettings(state: state)
			} header : {
				Text(AppointmentsRes.strings().appointments_settings_requests.desc().localized())
			} footer : {
				Text(AppointmentsRes.strings().appointments_settings_break_footer.desc().localized())
			}
			Section(AppointmentsRes.strings().appointments_settings_note_header.desc().localized()) {
				StateTextField(state.note, textEditor: true)
					.lineLimit(3...5)
					.textFieldStyle(.inList)
			}
			
			StateButton(state.save)
				.padding(.top, 16)
				.listRowInsets(EdgeInsets())
				.listRowBackground(Color.clear)
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
		StateSwitch(state: state.automaticApproval) { newValue in
			state.automaticApproval.onCheckedChange?(KotlinBoolean(bool: newValue))
		}
		
		StateTextField(state.minimalBreak)
			.textFieldStyle(.inListTrailing)
    }
}

private struct ScheduleStrip: View {
    let schedule: IOSScheduleState

    init(schedule: any ScheduleState) {
        self.schedule = IOSScheduleState.cast(schedule)
    }

    var body: some View {
        let days = schedule.asList()
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
        }
    }
}
