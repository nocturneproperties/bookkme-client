//
//  IOSAppointmentRequestState.swift
//  iosApp
//
//  Created by BookkMe on 25.06.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import shared
import SwiftUI

@Observable
@MainActor
class IOSAppointmentRequestState: @MainActor AppointmentRequestState, NativeStateRepresentation {

    typealias SwiftType = IOSAppointmentRequestState
    typealias KotlinType = AppointmentRequestState

    let requests: any ListState
    let notifications: any PresentationNotificationState

    init() {
        requests = IOSListState<IOSAppointmentRequestItemState>()
        notifications = IOSNotificationState()
    }

    func createAppointmentRequestItemState() -> any AppointmentRequestItemState {
        return IOSAppointmentRequestItemState()
    }
}
