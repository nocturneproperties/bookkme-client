//
//  AppointmentRequestScreen.swift
//  iosApp
//
//  Created by BookkMe on 25.06.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct AppointmentRequestSheet: View {

    @StateViewModel var viewModel: AppointmentRequestViewModel

    init() {
        self._viewModel = StateViewModel(
            wrappedValue: IosAppointmentsPresentationDiKt.appointmentRequestVM()
        )
    }

    var body: some View {
        let state = IOSAppointmentRequestState.cast(viewModel.uiState)
        let listState = IOSListState<IOSAppointmentRequestItemState>.cast(state.requests)
        ListGroup(listState: listState, listStyle: .plain) { item in
            RequestItemCard(item: item)
                .listRowSeparator(.hidden)
                .listRowBackground(Color.clear)
        }
		.padding(.top, 32)
        .sendLifecycleEventsTo(viewModel)
        .handleNotifications(state.notifications)
		.presentationBackground(AppColors.background)
		.presentationDragIndicator(.visible)
    }
}

@MainActor
private struct RequestItemCard: View {

    @Bindable var item: IOSAppointmentRequestItemState

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            VStack(alignment: .leading, spacing: 2) {
                Text(item.clientName)
                    .font(.headline)
                    .fontWeight(.semibold)
                    .foregroundStyle(AppColors.primary)
                Text(item.serviceName)
                    .font(.subheadline)
                    .foregroundStyle(AppColors.secondary)
            }

            HStack(spacing: 16) {
                Label(item.scheduledDate, systemImage: "calendar")
                    .font(.caption)
                    .foregroundStyle(AppColors.secondary)
                Label(item.scheduledTime, systemImage: "clock")
                    .font(.caption)
                    .foregroundStyle(AppColors.secondary)
            }

            if !item.note.isEmpty {
                Label(item.note, systemImage: "message")
                    .font(.caption)
                    .foregroundStyle(AppColors.secondary)
					.frame(maxWidth: .infinity, alignment: .leading)
					.padding(.horizontal, 8)
					.padding(.vertical, 8)
					.background(RoundedRectangle(cornerRadius: 12).fill(AppColors.primary.opacity(0.1)))
					.padding(.vertical, 8)
            }

            HStack(spacing: 8) {
                TextButton(item.declineButton)
                    .frame(maxWidth: .infinity)
					.buttonStyle(.negativeAction)
                StateButton(item.approveButton)
                    .frame(maxWidth: .infinity)
            }
        }
        .padding()
        .background(AppColors.elevated)
        .clipShape(RoundedRectangle(cornerRadius: 24))
    }
}
