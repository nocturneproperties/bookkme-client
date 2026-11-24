//
//  IOSAppointmentRequestItemState.swift
//  iosApp
//
//  Created by BookkMe on 26.06.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import shared
import SwiftUI

@Observable
@MainActor
class IOSAppointmentRequestItemState: IOSViewState, @MainActor AppointmentRequestItemState {

    var clientName: String = ""
    var clientInitials: String = ""
    var schedule: any StringDesc = RawStringDesc(string: "")
    var services: [any StringDesc] = []
    var price: String = ""
    var note: String = ""
    var onCallClick: (() -> Void)?
    let approveButton: any ButtonState
    let declineButton: any ButtonState

	init() {
        approveButton = IOSButtonState()
        declineButton = IOSButtonState()
        super.init()
    }
}
