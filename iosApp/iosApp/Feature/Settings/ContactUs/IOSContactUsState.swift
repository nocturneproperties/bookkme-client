//
//  IOSContactUsState.swift
//  iosApp
//
//  Created by BookkMe on 21.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared

@MainActor
@Observable
class IOSContactUsState: @MainActor ContactUsState {

	var appBar: any AppBarState
	
	var contactField: any TextFieldState
	
	var includeLogsSwitch: any SwitchState
	
	var logsExplanationText: any StringDesc
	
	var submitButton: any ButtonState
	
	var notifications: any PresentationNotificationState
	
	var navigation: any NavigationState
	
	init(initData: ContactUsStateInitData) {
		appBar = IOSAppBarState(title: initData.title)
		contactField = IOSTextFieldState(hint: initData.contactHint)
		includeLogsSwitch = IOSSwitchState(text: initData.usageLogsText, isChecked: false)
		logsExplanationText = initData.includeLogsExplanation
		submitButton = IOSButtonState(text: initData.submitButtonText, isEnabled: false)
		notifications = IOSNotificationState()
		navigation = IOSNavigationState()
	}
}
