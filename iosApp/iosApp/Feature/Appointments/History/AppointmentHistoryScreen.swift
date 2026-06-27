//
//  AppointmentHistoryScreen.swift
//  iosApp
//
//  Created by BookkMe on 24.06.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct AppointmentHistoryScreen: View {

    @EnvironmentObject var navigationStack: NavigationStackHolder
    @StateViewModel var viewModel: AppointmentHistoryViewModel

    init(businessId: KotlinUuid) {
        self._viewModel = StateViewModel(
            wrappedValue: IosAppointmentsPresentationDiKt.appointmentHistoryVM(businessId: businessId)
        )
    }

    var body: some View {
        let state = IOSAppointmentHistoryState.cast(viewModel.uiState)
        let listState = IOSListState<AppointmentHistoryItemState>.cast(state.appointments)
        ListGroup(listState: listState, listStyle: .plain) { item in
            AppointmentHistoryRow(state: item)
        }
        .searchable(
            text: state.searchField.binding(),
            placement: .navigationBarDrawer(displayMode: .always),
            prompt: state.searchField.placeholder.localized()
        )
        .refreshable { await state.refresh.impl().awaitRefresh() }
        .withNavigationBar(state.appBar)
        .sendLifecycleEventsTo(viewModel)
        .handleNotifications(state.notifications)
        .handleNavigation(state.navigation) { dest in
            switch dest {
            case let dest as AppointmentHistoryDestinations.AppointmentDetails:
                navigationStack.push(AppointmentsDestination.Details(appointmentId: dest.appointmentId))
            case is AppointmentHistoryDestinations.Back:
                navigationStack.popLast()
            default:
                break
            }
        }
    }
}

private struct AppointmentHistoryRow: View {

    let state: AppointmentHistoryItemState

    var body: some View {
        Button(action: state.onItemClick) {
            HStack(spacing: 16) {
                VStack(alignment: .leading, spacing: 2) {
                    Text(state.clientName)
                        .font(.headline)
                        .foregroundStyle(AppColors.primary)
                    Text(state.serviceName)
                        .font(.subheadline)
                        .foregroundStyle(AppColors.secondary)
                    Text(state.scheduledAt)
                        .font(.caption)
                        .foregroundStyle(AppColors.secondary)
                }

                Spacer()

                VStack(alignment: .trailing, spacing: 2) {
                    Text(state.earnings)
                        .font(.subheadline)
                        .fontWeight(.semibold)
                        .foregroundStyle(AppColors.primary)
                    StatusLabel(status: state.status)
                }
            }
            .frame(maxWidth: .infinity)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}
