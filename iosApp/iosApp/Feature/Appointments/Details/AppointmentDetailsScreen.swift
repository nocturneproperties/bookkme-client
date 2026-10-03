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
		ListGroup(listState: listState, listStyle: .insetGrouped, content: { section in
			InfoLineRow(line: section)
		}, header: {
			Section {
				StatusLabel(status: uiState.status)
					.listRowBackground(Color.clear)
					.listRowInsets(EdgeInsets())
			}
			.listSectionSeparator(.hidden)
			.listSectionSpacing(0)
		}, footer: {
			if uiState.rescheduleButton.isVisible {
				Section {
					StateButton(uiState.rescheduleButton)
						.listRowInsets(EdgeInsets())
						.listRowBackground(Color.clear)
				}
				.listSectionSpacing(.compact)
			}
			if uiState.servicePicker.isVisible {
				ServicesSection(
					servicePicker: uiState.servicePicker,
					subtotalLabel: uiState.subtotalLabel,
					subtotalPrice: uiState.subtotalPrice
				)
			}
		})
		.sheet(isPresented: Binding(
			get: { uiState.dateTimePicker.isDatePickerVisible },
			set: { uiState.dateTimePicker.isDatePickerVisible = $0 }
		)) { DateTimePicker(uiState.dateTimePicker) }
		.safeAreaInset(edge: .bottom) {
			CompletionActions(completeButton: uiState.completeButton, noShowButton: uiState.noShowButton)
		}
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

private struct CompletionActions: View {
	let completeButton: ButtonState
	let noShowButton: ButtonState

	var body: some View {
		if completeButton.isVisible || noShowButton.isVisible {
			VStack(spacing: 8) {
				if completeButton.isVisible {
					StateButton(completeButton)
				}
				if noShowButton.isVisible {
					TextButton(noShowButton)
						.buttonStyle(.negativeAction)
				}
			}
			.padding()
			.background(.bar)
		}
	}
}

private struct InfoLineRow: View {
	let line: InfoLine

	var body: some View {
		if let onClick = line.onClick {
			Button(action: onClick) {
				content
					.contentShape(Rectangle())
			}
			.buttonStyle(.plain)
		} else {
			content
		}
	}

	private var content: some View {
		LabeledContent(line.title.localized(), value: line.value.localized())
	}
}

private struct ServicesSection: View {
	let servicePicker: any OptionsMultiPickerState
	let subtotalLabel: any StringDesc
	let subtotalPrice: String

	var body: some View {
		Section {
			OptionsMultiPickerField(servicePicker) { item, _ in
				AppointmentServiceItem(service: item as! ServicePickerPresentation)
			}
			.listRowInsets(EdgeInsets())
		} header: {
			Text(servicePicker.pickerTitle.localized())
		} footer: {
			if !subtotalPrice.isEmpty {
				AppointmentSubtotalRow(label: subtotalLabel, price: subtotalPrice)
					.listRowInsets(EdgeInsets())
			}
		}
	}
}
