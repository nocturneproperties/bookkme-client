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

	var appointmentEnabled: any BooleanState
	var emailEnabled: any BooleanState
	var pushNotificationsEnabled: any BooleanState
	var telegramEnabled: any BooleanState

	var notifications: any PresentationNotificationState
	var navigation: any NavigationState

	init() {
		appBar = IOSAppBarState()
		appointmentEnabled = IOSBooleanState()
		emailEnabled = IOSBooleanState()
		pushNotificationsEnabled = IOSBooleanState()
		telegramEnabled = IOSBooleanState()
		notifications = IOSNotificationState()
		navigation = IOSNavigationState()
	}
}
