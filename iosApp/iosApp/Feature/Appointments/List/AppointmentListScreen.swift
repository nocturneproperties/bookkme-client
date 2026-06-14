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
		ListGroup(listState: listState, listStyle: .plain) { item in
			AppointmentRequestRow(state: item)
				.listRowSeparator(.hidden)
				.listRowBackground(Color.clear)
				.listRowInsets(EdgeInsets(top: 4, leading: 16, bottom: 4, trailing: 16))
		} header : {
			VStack {
				DateStrip(
					selectedDate: state.selectedDate,
					onDateSelected: state.onDateSelected
				)
				Divider()
					.background(AppColors.divider)
			}
			.listRowSeparator(.hidden)
			.listRowBackground(Color.clear)
			.listRowInsets(EdgeInsets())
		}
		.frame(maxHeight: .infinity)
		.refreshable { await state.refresh.impl().awaitRefresh() }
		.sheet(isPresented: Binding(
            get: { state.isDatePickerVisible },
            set: { state.isDatePickerVisible = $0 }
        )) {
            DatePickerSheet(
                selection: state.selectedDate,
                onDismiss: { state.isDatePickerVisible = false },
                onDatePicked: { date in
                    state.onDateSelected(date)
                    state.isDatePickerVisible = false
                }
            )
        }
		.withNavigationBar(state.appBar)
        .sendLifecycleEventsTo(viewModel)
        .handleNotifications(state.notifications)
        .handleNavigation(state.navigation) { dest in
            switch dest {
			case let dest as AppointmentListDestinations.CreateAppointment:
				navigationStack.push(AppointmentsDestination.Create(businessId: dest.businessId))
            default:
                break
            }
        }
    }
}

private struct DateStrip: View {

    let selectedDate: LocalDate
    let onDateSelected: (LocalDate) -> Void

    private var weekDates: [LocalDate] {
		let start = selectedDate.startOfWeek()

		return (DayOfWeek.monday.isoDayNumber...DayOfWeek.sunday.isoDayNumber).compactMap { day in
			return start.plus(value: day - 1, unit: DateTimeUnit.Companion().DAY)
        }
    }

    var body: some View {
        HStack(spacing: 0) {
            ForEach(weekDates, id: \.day) { date in
                DateCell(
                    date: date,
                    isSelected: date.compareTo(other: selectedDate) == 0,
                    onTap: { onDateSelected(date) }
                )
            }
        }
        .background(AppColors.background)
        .accessibilityIdentifier("date_strip")
    }
}

private struct DateCell: View {

    let date: LocalDate
    let isSelected: Bool
    let onTap: () -> Void

    private var isToday: Bool {
        date.compareTo(other: LocalDate.Companion().today(timeZone: TimeZone.Companion().currentSystemDefault())) == 0
    }

    var body: some View {
        VStack(spacing: 4) {
            Text(String(date.dayOfWeek.name.prefix(1)))
                .font(.caption2)
                .foregroundStyle(AppColors.secondary)
            Text("\(date.day)")
                .font(.system(size: 14, weight: .semibold))
                .frame(width: 30, height: 30)
                .background(
                    Circle().fill(isSelected ? AppColors.buttonPrimary : Color.clear)
                )
                .foregroundStyle(AppColors.primary)
                .animation(.easeInOut(duration: 0.15), value: isSelected)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 8)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(isToday ? AppColors.actionText : Color.clear, lineWidth: 1)
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
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
            .frame(maxWidth: .infinity)
            .background(AppColors.elevated)
            .clipShape(RoundedRectangle(cornerRadius: 12))
        }
        .buttonStyle(.plain)
    }
}
