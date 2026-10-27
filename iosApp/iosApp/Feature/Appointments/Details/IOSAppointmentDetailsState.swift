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
	var statusNote: (any StringDesc)?
	var infoSections: any ListState
	let servicePicker: any OptionsMultiPickerState
	var totalLabel: any StringDesc
	var totalPrice: String
	var originalPrice: String?
	let priceAdjustment: any PriceAdjustmentSectionState
	let rescheduleButton: any ButtonState
	let completeButton: any ButtonState
	let completion: any AppointmentCompletionState
	let noShowButton: any ButtonState
	let dateTimePicker: any DateTimePickerState
	var navigation: any NavigationState
	var notifications: any PresentationNotificationState

	init() {
		appBar = IOSAppBarState()
		status = UIAppointmentStatus(label: RawStringDesc(string: ""), color: .actiontext)
		infoSections = IOSListState<InfoLine>()
		servicePicker = IOSOptionsMultiPickerState()
		totalLabel = RawStringDesc(string: "")
		totalPrice = ""
		originalPrice = nil
		priceAdjustment = IOSPriceAdjustmentSectionState()
		navigation = IOSNavigationState()
		notifications = IOSNotificationState()
		rescheduleButton = IOSButtonState()
		completeButton = IOSButtonState()
		completion = IOSAppointmentCompletionState()
		noShowButton = IOSButtonState()
		dateTimePicker = IOSDateTimePickerState()
	}
}
