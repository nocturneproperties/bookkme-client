//
//  IOSNotificationSettingsState.swift
//  iosApp
//
//  Copyright © 2026 BookkMe. All rights reserved.
//

import shared

@MainActor
@Observable
class IOSNotificationSettingsState: @MainActor NotificationSettingsState {

	var appBar: any AppBarState

	var notifications: any PresentationNotificationState

	init() {
		appBar = IOSAppBarState()
		notifications = IOSNotificationState()
	}
}
