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
	var photo: any ButtonState
	var address: any TextFieldState
	var currency: any PickerFieldState
	var description_: any TextFieldState
	var instagram: any TextFieldState
	var location: any TextFieldState
	var name: any TextFieldState
	var save: any ButtonState
	var telegram: any TextFieldState
	var testLocation: any ButtonState
	var viber: any TextFieldState
	var phone: any TextFieldState
	
	var schedule: any ScheduleState
	
	var notifications: any PresentationNotificationState
	var navigation: any NavigationState
	
	init(initData: BusinessSettingsStateInitData) {
		self.appBar = IOSAppBarState(title: initData.title)
		self.photo = IOSButtonState(text: initData.addPhotoText)
		self.address = IOSTextFieldState(placeholder: initData.addressHint)
		self.currency = IOSPickerState()
		self.description_ = IOSTextFieldState(placeholder: initData.descriptionHint)
		self.instagram = IOSTextFieldState(placeholder: initData.instagramHint, startIcon: initData.instaIcon)
		self.location = IOSTextFieldState(placeholder: initData.locationHint, readOnly: true)
		self.name = IOSTextFieldState(placeholder: initData.nameHint)
		self.notifications = IOSNotificationState()
		self.save = IOSButtonState(text: initData.saveButtonText, isEnabled: false)
		self.telegram = IOSTextFieldState(placeholder: initData.telegramHint, startIcon: initData.telegramIcon)
		self.testLocation = IOSButtonState(text: initData.testLocationText)
		self.viber = IOSTextFieldState(placeholder: initData.viberHint, startIcon: initData.viberIcon)
		self.phone = IOSTextFieldState(placeholder: initData.phoneHint, startIcon: initData.phoneIcon)
		self.navigation = IOSNavigationState()
		self.schedule = IOSScheduleState()
	}
}
