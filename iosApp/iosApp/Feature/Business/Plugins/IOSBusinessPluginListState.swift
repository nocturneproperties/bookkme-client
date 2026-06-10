//
//  IOSBusinessPluginListState.swift
//  iosApp
//
//  Created by BookkMe on 10.06.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import shared
import SwiftUI

@Observable
@MainActor
class IOSBusinessPluginListState: @MainActor BusinessPluginListState {
	let appBar: any AppBarState
	
	let appointmentPlugin: any BusinessPluginState
	
	let navigation: any NavigationState
	let notifications: any PresentationNotificationState
	
	init() {
		appBar = IOSAppBarState()
		appointmentPlugin = IOSBusinessPluginState()
		navigation = IOSNavigationState()
		notifications = IOSNotificationState()
	}
}
