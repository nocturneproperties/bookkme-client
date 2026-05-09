//
//  IOSClientDetailsState.swift
//  iosApp
//
//  Created by BookkMe on 07.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import shared

@Observable
@MainActor
class IOSClientDetailsState: @MainActor ClientDetailsState {
	var appBar: any AppBarState
	var infoSections: any ListState
	var navigation: any NavigationState
	var notifications: any PresentationNotificationState
	
	init() {
		appBar = IOSAppBarState()
		infoSections = IOSListState<InfoLine>()
		navigation = IOSNavigationState()
		notifications = IOSNotificationState()
	}
	
	
}
