//
//  IOSAccountDeletionBannerState.swift
//  iosApp
//
//  Copyright © 2026 BookkMe. All rights reserved.
//

import shared

@MainActor
@Observable
class IOSAccountDeletionBannerState: @MainActor AccountDeletionBannerState {

	var isVisible: Bool

	var message: any StringDesc

	var cancelButton: any ButtonState

	var notifications: any PresentationNotificationState

	init() {
		isVisible = false
		message = RawStringDesc(string: "")
		cancelButton = IOSButtonState()
		notifications = IOSNotificationState()
	}
}
