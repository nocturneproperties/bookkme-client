//
//  IOSServiceListState.swift
//  iosApp
//
//  Created by BookkMe on 11.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import shared

@Observable
@MainActor
class IOSServiceListState: @MainActor ServiceListState, NativeStateRepresentation {
	
	typealias SwiftType = IOSServiceListState
	typealias KotlinType = ServiceListState
	
	
	var appBar: any AppBarState
	var refreshState: any RefreshState
	var searchField: any TextFieldState
	var groupsSection: Action
	var services: any ListState
	
	var navigation: any NavigationState
	var notifications: any PresentationNotificationState
	
	init() {
		appBar = IOSAppBarState()
		refreshState = IOSRefreshState()
		searchField = IOSTextFieldState()
		services = IOSListState<ServiceListStateServiceGroupUI>()
		groupsSection = Action(title: RawStringDesc(string: ""))
		navigation = IOSNavigationState()
		notifications = IOSNotificationState()
	}
}

extension ServiceListStateServiceGroupUI: @retroactive Identifiable {}
