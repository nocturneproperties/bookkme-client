//
//  IOSDeleteAccountState.swift
//  iosApp
//
//  Created by BookkMe on 19.03.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared

@MainActor
@Observable
class IOSDeleteAccountState: @MainActor DeleteAccountState {
	var appBar: any AppBarState
	
	var confirmationMessage: any StringDesc
	
	var confirmation: any BooleanState
	
	var deleteButton: any ButtonState
	
	var navigation: any NavigationState
	
	var notifications: any PresentationNotificationState
	
	init(initData: DeleteAccountStateInitData) {
		appBar = IOSAppBarState(title: initData.title)
		confirmationMessage = initData.confirmationMessage
		confirmation = IOSBooleanState(text: initData.switchMessage, isChecked: false)
		deleteButton = IOSButtonState(text: initData.buttonMessage, isEnabled: false)
		navigation = IOSNavigationState()
		notifications = IOSNotificationState()
	}
}
