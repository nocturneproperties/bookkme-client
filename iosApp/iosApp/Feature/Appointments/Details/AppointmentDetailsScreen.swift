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
				HStack {
					StatusLabel(status: uiState.status)
					Spacer()
					if uiState.noShowButton.isVisible {
						TextButton(uiState.noShowButton, textAlignment: .trailing)
							.buttonStyle(.negativeAction)
							.fixedSize()
					}
					if uiState.rescheduleButton.isVisible {
						TextButton(uiState.rescheduleButton, textAlignment: .trailing)
							.buttonStyle(.textAction)
							.fixedSize()
					}
				}
				.frame(minHeight: 44)
				.listRowBackground(Color.clear)
					.listRowInsets(EdgeInsets())
			}
			.listSectionSeparator(.hidden)
			.listSectionSpacing(0)
		}, footer: {
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
			CompleteAction(completeButton: uiState.completeButton)
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

private struct CompleteAction: View {
	let completeButton: ButtonState

	var body: some View {
		if completeButton.isVisible {
			StateButton(completeButton)
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
