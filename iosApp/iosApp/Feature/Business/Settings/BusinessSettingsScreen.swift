//
//  BusinessSettingsScreen.swift
//  iosApp
//
//  Created by BookkMe on 07.09.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//
import SwiftUI
import shared

struct BusinessSettingsScreen: View {
	
	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: BusinessSettingsViewModel
	
	init(id: shared.KotlinUuid) {
		self._viewModel = StateViewModel(wrappedValue: IOSBusinessDiKt.businessSettingsVM(id: id))
	}
	
	var body: some View {
		List {
			BusinessSettingsContent(viewModel: viewModel)
		}
		.toolbar {
			TextButton(viewModel.uiState.save) {
				viewModel.onSaveClick()
			}
		}
		.listSectionSpacing(.compact)
		.scrollDismissesKeyboard(.immediately)
		.withNavigationBar(viewModel.uiState.appBar)
		.handleNotifications(viewModel.uiState.notifications)
		.sendLifecycleEventsTo(viewModel)
		.handleNavigation(viewModel.uiState.navigation) { dest in
			switch dest {
			case is BusinessSettingsDestination.Back:
				navigationStack.popLast()
			default :
				break
			}
		}
	}
}

struct BusinessSettingsContent: View {
	
	let state: IOSBusinessSettingsState
	let viewModel: BusinessSettingsViewModel
	
	init(viewModel: BusinessSettingsViewModel) {
		self.state = IOSBusinessSettingsState.cast(viewModel.uiState)
		self.viewModel = viewModel
	}
	
	var body: some View {
		Section(BusinessRes.strings().business_settings_name_title.desc().localized()) {
			StateTextField(state.name) { text in
				viewModel.onNameChanged(name: text)
			}
			.textFieldStyle(.inList)
		}
		
		Section(BusinessRes.strings().business_settings_description_title.desc().localized()) {
			StateTextField(state.description_, textEditor: true) { text in
				viewModel.onDescriptionChanged(description: text)
			}
			.lineLimit(3, reservesSpace: true)
			.textFieldStyle(.inList)
		}
		
		let scheduleDays = IOSScheduleState.cast(state.schedule).list.items(DaySettingsState.self)
		Section(BusinessRes.strings().business_settings_schedule.desc().localized()) {
			ScheduleStrip(schedule: scheduleDays)
				.listRowInsets(EdgeInsets())
			if let expanded = scheduleDays.first(where: { $0.isVisible }) {
				ScheduleDay(state: expanded)
					.listRowInsets(EdgeInsets())
			}
		}
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
		}
		
		Section {
			StateTextField(state.location) { text in
			}
			.textFieldStyle(.inList)
			
			TextButton(state.pickLocation, textAlignment: .leading) {
				viewModel.onPickLocationClicked()
			}
		} header : {
			Text(BusinessRes.strings().business_settings_location_title.desc().localized())
		} footer : {
			TextButton(state.testLocation, textAlignment: .leading) {
				viewModel.onTestLocationClick()
			}
		}
		
		Section(BusinessRes.strings().business_settings_address_title.desc().localized()) {
			StateTextField(state.address) { text in
				viewModel.onAddressChanged(address: text)
			}
			.textFieldStyle(.inList)
		}
		
		Section(BusinessRes.strings().business_settings_currency_title.desc().localized()) {
			PickerField(state.currency) { option in
				viewModel.onCurrencySelected(currencyUI: option as! CurrencyUI)
			}
			.textFieldStyle(.inList)
		}
		
		Section(BusinessRes.strings().business_settings_socials_title.desc().localized()) {
			StateTextField(state.instagram) { text in
				viewModel.onInstagramChanged(insta: text)
			}
			.textFieldStyle(.inList)
			
			StateTextField(state.telegram) { text in
				viewModel.onTelegramChanged(telegram: text)
			}
			.textFieldStyle(.inList)
			
			StateTextField(state.viber) { text in
				viewModel.onViberChanged(viber: text)
			}
			.textFieldStyle(.inList)
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

#Preview {
	NavigationStack {
		BusinessSettingsScreen(id: shared.KotlinUuid.companion.random())
	}
}
