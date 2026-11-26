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
	
	var chatConfirmation: any ChatConfirmationState
	
	var navigation: any NavigationState
	
	var notifications: any PresentationNotificationState
	
	init() {
		appBar = IOSAppBarState()
		confirmationMessage = RawStringDesc(string: "")
		confirmation = IOSBooleanState()
		deleteButton = IOSButtonState()
		chatConfirmation = IOSChatConfirmationState()
		navigation = IOSNavigationState()
		notifications = IOSNotificationState()
	}
}
