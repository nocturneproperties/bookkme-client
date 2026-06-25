//
//  IOSAppointmentHistoryState.swift
//  iosApp
//
//  Created by BookkMe on 24.06.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import shared
import SwiftUI

@Observable
@MainActor
class IOSAppointmentHistoryState: @MainActor AppointmentHistoryState, NativeStateRepresentation {

    typealias SwiftType = IOSAppointmentHistoryState
    typealias KotlinType = AppointmentHistoryState

    let appBar: any AppBarState
    let searchField: any TextFieldState
    let appointments: any ListState
    let refresh: any RefreshState
    let navigation: any NavigationState
    let notifications: any PresentationNotificationState

    init() {
        appBar = IOSAppBarState()
        searchField = IOSTextFieldState()
        appointments = IOSListState<AppointmentHistoryItemState>()
        refresh = IOSRefreshState()
        navigation = IOSNavigationState()
        notifications = IOSNotificationState()
    }
}

extension AppointmentHistoryItemState: @retroactive Identifiable {}
