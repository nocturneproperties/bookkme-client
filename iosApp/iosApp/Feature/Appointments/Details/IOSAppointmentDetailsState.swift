//
//  IOSAppointmentDetailsState.swift
//  iosApp
//
//  Created by BookkMe on 16.06.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

@Observable
@MainActor
class IOSAppointmentDetailsState: @MainActor AppointmentDetailsState {
	
	var appBar: any AppBarState
	var status: UIAppointmentStatus
	var infoSections: any ListState
	let servicePicker: any OptionsMultiPickerState
	var subtotalLabel: any StringDesc
	var subtotalPrice: String
	let rescheduleButton: any ButtonState
	let completeButton: any ButtonState
	let noShowButton: any ButtonState
	let dateTimePicker: any DateTimePickerState
	var navigation: any NavigationState
	var notifications: any PresentationNotificationState

	init() {
		appBar = IOSAppBarState()
		status = UIAppointmentStatus(label: RawStringDesc(string: ""), color: .actiontext)
		infoSections = IOSListState<InfoLine>()
		servicePicker = IOSOptionsMultiPickerState()
		subtotalLabel = RawStringDesc(string: "")
		subtotalPrice = ""
		navigation = IOSNavigationState()
		notifications = IOSNotificationState()
		rescheduleButton = IOSButtonState()
		completeButton = IOSButtonState()
		noShowButton = IOSButtonState()
		dateTimePicker = IOSDateTimePickerState()
	}
}
