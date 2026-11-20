//
//  IOSEditClientState.swift
//  iosApp
//
//  Created by BookkMe on 02.09.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import shared

@MainActor
@Observable
class IOSEditClientState: @MainActor EditClientState {

	var appBar: any AppBarState
	var isAttachedInfoVisible: Bool
	var attachedInfoText: any StringDesc
	var name: any TextFieldState
	var lastName: any TextFieldState
	var phone: any TextFieldState
	var email: any TextFieldState
	var description_: any TextFieldState
	var submit: any ButtonState
	var deleteButton: any ButtonState
	var navigation: any NavigationState
	var notifications: any PresentationNotificationState

	init() {
		appBar = IOSAppBarState()
		isAttachedInfoVisible = false
		attachedInfoText = RawStringDesc(string: "")
		name = IOSTextFieldState()
		lastName = IOSTextFieldState()
		phone = IOSTextFieldState()
		email = IOSTextFieldState()
		description_ = IOSTextFieldState()
		submit = IOSButtonState()
		deleteButton = IOSButtonState()
		navigation = IOSNavigationState()
		notifications = IOSNotificationState()
	}
}
