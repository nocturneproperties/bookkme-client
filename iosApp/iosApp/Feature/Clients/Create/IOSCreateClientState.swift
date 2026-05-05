//
//  IOSCreateClientState.swift
//  iosApp
//
//  Created by BookkMe on 05.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import shared

@MainActor
@Observable
class IOSCreateClientState: @MainActor CreateClientState {
	
	var appBar: any AppBarState
	var lastName: any TextFieldState
	var name: any TextFieldState
	var navigation: any NavigationState
	var notifications: any PresentationNotificationState
	var phone: any TextFieldState
	var submit: any ButtonState
	
	init() {
		appBar = IOSAppBarState()
		lastName = IOSTextFieldState()
		name = IOSTextFieldState()
		navigation = IOSNavigationState()
		notifications = IOSNotificationState()
		phone = IOSTextFieldState()
		submit = IOSButtonState()
	}
	
}
