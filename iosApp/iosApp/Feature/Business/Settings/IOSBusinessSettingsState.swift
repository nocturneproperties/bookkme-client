//
//  IOSBusinessSettingsState.swift
//  iosApp
//
//  Created by BookkMe on 06.09.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared
import SwiftUI

@MainActor
class IOSBusinessSettingsState: @MainActor BusinessSettingsState, NativeStateRepresentation {
	
	typealias SwiftType = IOSBusinessSettingsState
	
	typealias KotlinType = BusinessSettingsState
	
	
	var appBar: any AppBarState
	
	var address: any TextFieldState
	
	var currency: any PickerFieldState
	
	var description_: any TextFieldState
	
	var instagram: any TextFieldState
	
	var location: any TextFieldState
	
	var name: any TextFieldState
	
	var notifications: any PresentationNotificationState
	
	var save: any ButtonState
	
	var telegram: any TextFieldState
	
	var testLocation: any ButtonState
	
	var pickLocation: any ButtonState
	
	var viber: any TextFieldState
	
	var phone: any TextFieldState
	
	init(initData: BusinessSettingsStateInitData) {
		self.appBar = IOSAppBarState(title: initData.title)
		self.address = IOSTextFieldState(hint: initData.addressHint)
		self.currency = IOSPickerState(textField: IOSTextFieldState())
		self.description_ = IOSTextFieldState(hint: initData.descriptionHint)
		self.instagram = IOSTextFieldState(hint: initData.instagramHint, startIcon: initData.instaIcon)
		self.location = IOSTextFieldState(hint: initData.locationHint, readOnly: true)
		self.name = IOSTextFieldState(hint: initData.nameHint)
		self.notifications = IOSNotificationState()
		self.save = IOSButtonState(text: initData.saveButtonText, isEnabled: false)
		self.telegram = IOSTextFieldState(hint: initData.telegramHint, startIcon: initData.telegramIcon)
		self.testLocation = IOSButtonState(text: initData.testLocationText)
		self.viber = IOSTextFieldState(hint: initData.viberHint, startIcon: initData.viberIcon)
		self.pickLocation = IOSButtonState(text: initData.pickLocationText)
		self.phone = IOSTextFieldState(hint: initData.phoneHint, startIcon: initData.phoneIcon)
	}
	
}
