//
//  EditProfileState.swift
//  iosApp
//
//  Created by BookkMe on 16.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared
import SwiftUI

@MainActor
@Observable
class IOSEditProfileState: @MainActor EditProfileState {
	
	var appBar: any AppBarState
	
	var name: any TextFieldState
	
	var email: any TextFieldState
	
	var changeEmail: any ButtonState
	
	var confirmButton: any ButtonState
	
	var notification: any PresentationNotificationState
	
	var navigation: any NavigationState
	
	init() {
		appBar = IOSAppBarState()
		confirmButton = IOSButtonState()
		name = IOSTextFieldState()
		email = IOSTextFieldState()
		changeEmail = IOSButtonState()
		notification = IOSNotificationState()
		navigation = IOSNavigationState()
	}
}
