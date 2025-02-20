//
//  EditProfileState.swift
//  iosApp
//
//  Created by BookkMe on 16.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared
import SwiftUI

class IOSEditProfileState: EditProfileState {
	var appBar: any AppBarState
	
	var name: any TextFieldState
	
	var lastName: any TextFieldState
	
	var email: any TextFieldState
	
	var confirmButton: any ButtonState
	
	var notification: any PresentationNotificationState
	
	init(initData:EditProfileStateInitData) {
		appBar = IOSAppBarState(title: initData.title)
		confirmButton = IOSButtonState(text: initData.confirmButtonText, isEnabled: false)
		email = IOSTextFieldState(hint: initData.emailHint)
		lastName = IOSTextFieldState(hint: initData.lastNameHint)
		name = IOSTextFieldState(hint: initData.nameHint)
		notification = IOSNotificationState()
	}
}
