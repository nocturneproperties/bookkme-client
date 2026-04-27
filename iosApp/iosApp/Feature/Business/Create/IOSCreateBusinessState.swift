//
//  IOSCreateBusinessState.swift
//  iosApp
//
//  Created by BookkMe on 08.05.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared

@MainActor
@Observable
class IOSCreateBusinessState: @MainActor CreateBusinessState {
	
	var appBar: any AppBarState
	
	var createBtn: any ButtonState
	
	var name: any TextFieldState
	
	var notifications: any PresentationNotificationState
	
	init(initData: CreateBusinessStateInitData) {
		appBar = IOSAppBarState(title: initData.title)
		createBtn = IOSButtonState(text: initData.buttonText, isEnabled: false)
		name = IOSTextFieldState(supportingTextRes: initData.supportingText, hint: initData.hint)
		notifications = IOSNotificationState()
	}
	
}

extension CreateBusinessState {
	func impl() -> IOSCreateBusinessState {
		return self as! IOSCreateBusinessState
	}
}
