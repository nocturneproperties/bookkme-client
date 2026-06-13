//
//  IOSAppointmentRequestListState.swift
//  iosApp
//
//  Created by BookkMe on 11.06.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import shared
import SwiftUI

@Observable
@MainActor
class IOSAppointmentListState: @MainActor AppointmentListState, NativeStateRepresentation {

    typealias SwiftType = IOSAppointmentListState
    typealias KotlinType = AppointmentListState

    let appBar: any AppBarState
	let refresh: any RefreshState
	var selectedDate: LocalDate
	let appointments: any ListState
	var onDateSelected: (LocalDate) -> Void
    var isLoading: Bool
    var isDatePickerVisible: Bool
    let navigation: any NavigationState
    let notifications: any PresentationNotificationState

    init() {
        appBar = IOSAppBarState()
		refresh = IOSRefreshState()
		selectedDate = LocalDate.Companion().today(timeZone: TimeZone.Companion().currentSystemDefault())
		appointments = IOSListState<AppointmentItemState>()
        isLoading = false
        isDatePickerVisible = false
		onDateSelected = { _ in }
        navigation = IOSNavigationState()
        notifications = IOSNotificationState()
    }
}

extension AppointmentItemState: @retroactive Identifiable {}
