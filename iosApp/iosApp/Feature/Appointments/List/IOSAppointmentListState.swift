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
    let requestsButton: any ButtonState
	let refresh: any RefreshState
	let dates: any ListState
	let appointments: any ListState
	let datePicker: any DatePickerState
	var requestsBusinessId: KotlinUuid?
    let navigation: any NavigationState
    let notifications: any PresentationNotificationState

    init() {
        appBar = IOSAppBarState()
        requestsButton = IOSButtonState()
		refresh = IOSRefreshState()
		datePicker = IOSDatePickerState()
		dates = IOSListState<DateInfo>()
		appointments = IOSListState<AppointmentItemState>()
        navigation = IOSNavigationState()
        notifications = IOSNotificationState()
    }
}

extension AppointmentItemState: @retroactive Identifiable {}
