//
//  AppointmentListScreen.swift
//  iosApp
//
//  Created by BookkMe on 11.06.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct AppointmentListScreen: View {

    @EnvironmentObject var navigationStack: NavigationStackHolder
    @StateViewModel var viewModel: AppointmentListViewModel

    init() {
        self._viewModel = StateViewModel(
            wrappedValue: IosAppointmentsPresentationDiKt.appointmentListVM()
        )
    }

    var body: some View {
        let state = IOSAppointmentListState.cast(viewModel.uiState)
        let listState = IOSListState<AppointmentItemState>.cast(state.appointments)
		let dateList = IOSListState<DateInfo>.cast(state.dates)
        let today = LocalDate.Companion().today(timeZone: TimeZone.Companion().currentSystemDefault())
		ListGroup(listState: listState, listStyle: .automatic) { item in
			AppointmentRequestRow(state: item)
				.listRowSeparator(.hidden)
		} header : {
			VStack {
				DateStrip(
					selectedDate: state.datePicker.pickedDate ?? today,
					days: dateList.typedItems,
					onDateSelected: { state.datePicker.onDatePicked?($0) }
				)
				Divider()
					.background(AppColors.divider)
			}
			.listRowSeparator(.hidden)
			.listRowBackground(AppColors.background)
			.listRowInsets(EdgeInsets())
		}
		.toolbar {
			ToolbarItem(placement: .navigation) {
				TextButton(state.requestsButton)
			}
		}
		.frame(maxHeight: .infinity)
		.refreshable { await state.refresh.impl().awaitRefresh() }
		.sheet(isPresented: Binding(
            get: { state.datePicker.isDatePickerVisible },
            set: { state.datePicker.isDatePickerVisible = $0 }
        )) {
            AppDatePicker(state: state.datePicker)
        }
		.sheet(item: Binding(get: {
			state.requestsBusinessId
		}, set: {
			state.requestsBusinessId = $0
		})) { businessId in
            AppointmentRequestSheet(businessId: businessId)
        }
		.listSectionSpacing(.compact)
		.withNavigationBar(state.appBar)
        .sendLifecycleEventsTo(viewModel)
        .handleNotifications(state.notifications)
        .handleNavigation(state.navigation) { dest in
            switch dest {
			case let dest as AppointmentListDestinations.CreateAppointment:
				navigationStack.push(AppointmentsDestination.Create(businessId: dest.businessId))
			case let dest as AppointmentListDestinations.AppointmentDetails:
				navigationStack.push(AppointmentsDestination.Details(appointmentId: dest.appointmentId))
            default:
                break
            }
        }
    }
}

private struct DateStrip: View {

    let selectedDate: LocalDate
	let days: [DateInfo]
    let onDateSelected: (LocalDate) -> Void

    var body: some View {
        HStack(spacing: 0) {
			ForEach(days, id: \.date.day) { day in
                DateCell(
					day: day,
					isSelected: day.date.compareTo(other: selectedDate) == 0,
                    onTap: { onDateSelected(day.date) }
                )
            }
        }
        .accessibilityIdentifier("date_strip")
    }
}

private struct DateCell: View {

    let day: DateInfo
    let isSelected: Bool
    let onTap: () -> Void

    var body: some View {
        VStack(spacing: 4) {
			Text(day.str)
                .font(.caption2)
                .foregroundStyle(AppColors.secondary)
			Text("\(day.date.day)")
                .font(.system(size: 14, weight: .semibold))
                .frame(width: 30, height: 30)
                .background(
                    Circle().fill(isSelected ? AppColors.buttonPrimary : Color.clear)
                )
				.foregroundStyle(isSelected ? AppColors.onAction : AppColors.primary)
                .animation(.easeInOut(duration: 0.15), value: isSelected)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 8)
        .overlay(
            RoundedRectangle(cornerRadius: 24)
				.stroke(day.isToday ? AppColors.actionText : Color.clear, lineWidth: 1)
				.padding(1)
        )
        .contentShape(Rectangle())
        .onTapGesture { onTap() }
        .accessibilityAddTraits(.isButton)
        .accessibilityElement(children: .combine)
    }
}

private struct AppointmentRequestRow: View {

    let state: AppointmentItemState

    var body: some View {
		Section {
			Button(action: state.onItemClick) {
				HStack(spacing: 16) {
					Text(state.scheduledAt)
						.font(.title2)
						.fontWeight(.medium)
						.foregroundStyle(AppColors.primary)
						.fixedSize()

					VStack(alignment: .leading, spacing: 2) {
						Text(state.clientName)
							.font(.headline)
							.foregroundStyle(AppColors.primary)
						Text(state.serviceName)
							.font(.subheadline)
							.foregroundStyle(AppColors.secondary)
					}

					Spacer()

					Text(state.earnings)
						.font(.subheadline)
						.fontWeight(.semibold)
						.foregroundStyle(AppColors.primary)
				}
				.frame(maxWidth: .infinity)
				.contentShape(Rectangle())
			}
			.buttonStyle(.plain)
		}
    }
}
