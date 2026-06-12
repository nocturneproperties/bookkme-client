//
//  AppointmentRequestListScreen.swift
//  iosApp
//
//  Created by BookkMe on 11.06.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct AppointmentRequestListScreen: View {

    @EnvironmentObject var navigationStack: NavigationStackHolder
    @StateViewModel var viewModel: AppointmentRequestListViewModel

    init() {
        self._viewModel = StateViewModel(
            wrappedValue: IosAppointmentsPresentationDiKt.appointmentRequestListVM()
        )
    }

    private var state: IOSAppointmentListState {
        IOSAppointmentListState.cast(viewModel.uiState)
    }

    var body: some View {
        ZStack {
            
        }
        .withNavigationBar(state.appBar)
        .sendLifecycleEventsTo(viewModel)
        .handleNotifications(state.notifications)
        .handleNavigation(state.navigation) { dest in
            switch dest {
            default:
                break
            }
        }
    }
}

private struct AppointmentRequestRow: View {

    let state: AppointmentItemState

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(state.clientName.localized())
                .font(.headline)
            Text(state.serviceName.localized())
                .font(.subheadline)
                .foregroundColor(.secondary)
            Divider()
                .padding(.vertical, 4)
            HStack {
                Text(state.scheduledAt.localized())
                    .font(.caption)
                Spacer()
            }
        }
        .padding(.vertical, 8)
    }
}
