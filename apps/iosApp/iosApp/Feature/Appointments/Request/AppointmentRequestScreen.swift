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
        VStack(alignment: .leading, spacing: 8) {
            RequestsHeader(state: state)
                .padding(.horizontal, 20)
                .padding(.top, 28)
            ListGroup(listState: listState, listStyle: .plain) { item in
                RequestItemCard(item: item)
                    .listRowSeparator(.hidden)
                    .listRowBackground(Color.clear)
            }
        }
        .sendLifecycleEventsTo(viewModel)
        .handleNotifications(state.notifications)
		.presentationBackground(AppColors.background)
		.presentationDragIndicator(.visible)
    }
}

@MainActor
private struct RequestsHeader: View {

    @Bindable var state: IOSAppointmentRequestState

    var body: some View {
        Text(state.title.localized())
            .font(.title2)
            .fontWeight(.semibold)
            .foregroundStyle(AppColors.primary)
    }
}

@MainActor
private struct RequestItemCard: View {

    @Bindable var item: IOSAppointmentRequestItemState

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            ClientRow(item: item)
            ServicesRow(item: item)
            if !item.note.isEmpty {
                Text(item.note)
                    .font(.subheadline)
                    .italic()
                    .foregroundStyle(AppColors.secondary)
            }
            HStack(spacing: 8) {
                NegativeStateButton(item.declineButton)
                StateButton(item.approveButton)
            }
        }
        .padding()
        .background(AppColors.elevated)
        .clipShape(RoundedRectangle(cornerRadius: 24))
    }
}

@MainActor
private struct ClientRow: View {

    @Bindable var item: IOSAppointmentRequestItemState

    var body: some View {
        HStack(spacing: 12) {
            Text(item.clientInitials)
                .font(.subheadline)
                .fontWeight(.semibold)
                .foregroundStyle(AppColors.actionText)
                .frame(width: 40, height: 40)
                .background(Circle().fill(AppColors.actionText.opacity(0.15)))
            VStack(alignment: .leading, spacing: 2) {
                Text(item.clientName)
                    .font(.headline)
                    .foregroundStyle(AppColors.primary)
                Label(item.schedule.localized(), systemImage: "calendar")
                    .font(.subheadline)
                    .foregroundStyle(AppColors.secondary)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            if let onCallClick = item.onCallClick {
                Button(action: onCallClick) {
                    Image(systemName: "phone")
                        .font(.body)
                        .foregroundStyle(AppColors.actionText)
                        .frame(width: 40, height: 40)
                }
                .buttonStyle(.plain)
                .accessibilityLabel(AppointmentsRes.strings().appointments_details_phone.desc().localized())
            }
        }
    }
}

@MainActor
private struct ServicesRow: View {

    @Bindable var item: IOSAppointmentRequestItemState

    var body: some View {
        HStack(alignment: .center, spacing: 8) {
            FlowLayout(spacing: 6) {
                ForEach(Array(item.services.enumerated()), id: \.offset) { _, service in
                    Text(service.localized())
                        .font(.caption)
                        .foregroundStyle(AppColors.secondary)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(RoundedRectangle(cornerRadius: 8).fill(AppColors.background))
                        .overlay(RoundedRectangle(cornerRadius: 8).stroke(AppColors.divider, lineWidth: 1))
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            Text(item.price)
                .font(.subheadline)
                .fontWeight(.semibold)
                .foregroundStyle(AppColors.primary)
        }
    }
}
