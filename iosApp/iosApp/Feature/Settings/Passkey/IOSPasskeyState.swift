//
//  IOSPasskeyState.swift
//  iosApp
//
//  Created by BookkMe on 29.03.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared
import SwiftUI

class IOSPasskeyState: PasskeyState, ObservableObject {
	
	var addPasskeyButton: any ButtonState
	
	var appBar: any AppBarState
	
	var notification: any PresentationNotificationState
	
	var refresh: any RefreshState
	
	@Published
	var passkeys: [PasskeyStatePasskeyItem]
	
	init(initData: PasskeyStateInitData) {
		addPasskeyButton = IOSButtonState(text: initData.addButtonText)
		appBar = IOSAppBarState(title: initData.title)
		notification = IOSNotificationState()
		passkeys = []
		refresh = IOSRefreshState()
	}
	
	func replacePasskeyList(items: [PasskeyStatePasskeyItem]) {
		passkeys = items
	}
}

extension PasskeyState {
	func impl() -> IOSPasskeyState {
		return self as! IOSPasskeyState
	}
}
