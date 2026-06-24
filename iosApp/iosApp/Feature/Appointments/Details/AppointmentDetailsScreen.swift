//
//  AppointmentDetailsScreen.swift
//  iosApp
//
//  Created by BookkMe on 16.06.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct AppointmentDetailsScreen: View {

	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: AppointmentDetailsViewModel

	init(appointmentId: KotlinUuid) {
		_viewModel = StateViewModel(
			wrappedValue: IosAppointmentsPresentationDiKt.appointmentDetailsVM(appointmentId: appointmentId)
		)
	}

	var body: some View {
		let uiState = viewModel.uiState
		let listState = IOSListState<InfoLine>.cast(uiState.infoSections)
		ListGroup(listState: listState, listStyle: .plain, content: { section in
			Group {
				if (section.id == AppointmentDetailsStateCompanion().APPOINTMENT_DATE_ID) {
					RescheduleInfoSection(section: section, rescheduleButton: uiState.rescheduleButton)
				} else {
					InfoSection(section: section)
				}
			}
		}, header: {
			StatusLabel(status: uiState.status)
				.listRowSeparator(.hidden)
		})
		.sheet(isPresented: Binding(
			get: { uiState.dateTimePicker.isDatePickerVisible },
			set: { uiState.dateTimePicker.isDatePickerVisible = $0 }
		)) { DateTimePicker(uiState.dateTimePicker) }
		.withNavigationBar(uiState.appBar)
		.handleNotifications(uiState.notifications)
		.sendLifecycleEventsTo(viewModel)
		.handleNavigation(uiState.navigation) { dest in
			switch dest {
			case is AppointmentDetailsDestination.Back:
				navigationStack.popLast()
			default :
				break
			}
		}
	}
}

private struct RescheduleInfoSection: View {
	let section: InfoLine
	let rescheduleButton: ButtonState
	
	var body: some View {
		HStack {
			InfoSection(section: section)
			TextButton(rescheduleButton)
				.buttonStyle(.textAction)
		}
	}
}

private struct StatusLabel: View {
	let status: UIAppointmentStatus

	var body: some View {
		let color = status.color.color
		Text(status.label.localized())
			.font(.subheadline.weight(.semibold))
			.foregroundStyle(color)
			.padding(.horizontal, 12)
			.padding(.vertical, 4)
			.background(color.opacity(0.12))
			.clipShape(RoundedRectangle(cornerRadius: 8))
	}
}
