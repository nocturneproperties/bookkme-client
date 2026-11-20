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
					if let statusNote = uiState.statusNote {
						Text(statusNote.localized())
							.font(.subheadline)
							.foregroundStyle(AppColors.secondary)
					}
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
				ServicesSection(servicePicker: uiState.servicePicker)
			}
			PriceAdjustmentSection(state: uiState.priceAdjustment)
			TotalSection(label: uiState.totalLabel, price: uiState.totalPrice, originalPrice: uiState.originalPrice)
			CompleteAction(completeButton: uiState.completeButton)
		})
		.sheet(isPresented: Binding(
			get: { uiState.dateTimePicker.isDatePickerVisible },
			set: { uiState.dateTimePicker.isDatePickerVisible = $0 }
		)) { DateTimePicker(uiState.dateTimePicker) }
		.sheet(isPresented: Binding(
			get: { uiState.completion.isSheetVisible },
			set: { uiState.completion.isSheetVisible = $0 }
		)) { AppointmentCompletionSheet(uiState.completion) }
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
			Section {
				StateButton(completeButton)
					.listRowInsets(EdgeInsets())
					.listRowBackground(Color.clear)
			}
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

	var body: some View {
		Section {
			OptionsMultiPickerField(servicePicker) { item, _ in
				AppointmentServiceItem(service: item as! ServicePickerPresentation)
			}
			.listRowInsets(EdgeInsets())
		} header: {
			Text(servicePicker.pickerTitle.localized())
		}
	}
}

private struct PriceAdjustmentSection: View {
	let state: IOSPriceAdjustmentSectionState

	init(state: PriceAdjustmentSectionState) {
		self.state = IOSPriceAdjustmentSectionState.cast(state)
	}

	var body: some View {
		if state.isVisible {
			Section {
				OptionsMultiPickerField(state.servicePicker) { item, _ in
					AppointmentServiceItem(service: item as! ServicePickerPresentation)
				}
				.listRowInsets(EdgeInsets())
			} header: {
				Text(state.servicePicker.pickerTitle.localized())
			}
		}
		if let reason = state.reason {
			Section {
				Text(reason.localized())
			} header: {
				Text(state.reasonTitle.localized())
			}
		}
	}
}

private struct TotalSection: View {
	let label: any StringDesc
	let price: String
	let originalPrice: String?

	var body: some View {
		Section {
			HStack {
				Text(label.localized())
					.font(.title3.weight(.semibold))
					.foregroundStyle(AppColors.primary)
				Spacer()
				VStack(alignment: .trailing, spacing: 2) {
					Text(price)
						.font(.title3.weight(.semibold))
						.foregroundStyle(AppColors.primary)
					if let originalPrice {
						Text(originalPrice)
							.font(.caption)
							.strikethrough()
							.foregroundStyle(AppColors.secondary)
					}
				}
			}
			.padding(.horizontal, 16)
			.listRowInsets(EdgeInsets())
			.listRowBackground(Color.clear)
		}
	}
}
