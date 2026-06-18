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
	let appointments: any ListState
	let datePicker: any DatePickerState
    var isLoading: Bool
    let navigation: any NavigationState
    let notifications: any PresentationNotificationState

    init() {
        appBar = IOSAppBarState()
		refresh = IOSRefreshState()
		datePicker = IOSDatePickerState()
		appointments = IOSListState<AppointmentItemState>()
        isLoading = false
        navigation = IOSNavigationState()
        notifications = IOSNotificationState()
    }
}

extension AppointmentItemState: @retroactive Identifiable {}
