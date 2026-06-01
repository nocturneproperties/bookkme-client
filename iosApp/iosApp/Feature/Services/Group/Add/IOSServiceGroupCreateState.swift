//
//  IOSServiceGroupCreateState.swift
//  iosApp
//
//  Created by BookkMe on 30.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import shared
import SwiftUI

@MainActor
@Observable
class IOSAddGroupState: @MainActor AddGroupState {
	var title: any StringDesc
	let name: any TextFieldState
	let create: any ButtonState
	
	let navigation: any NavigationState
	let notifications: any PresentationNotificationState
	
	init() {
		title = RawStringDesc(string: "")
		name = IOSTextFieldState()
		create = IOSButtonState()
		navigation = IOSNavigationState()
		notifications = IOSNotificationState()
	}
}
