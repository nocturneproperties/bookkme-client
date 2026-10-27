//
//  IOSClientsListState.swift
//  iosApp
//
//  Created by BookkMe on 03.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//
import SwiftUI
import shared

@Observable
@MainActor
class IOSClientsListState: @MainActor ClientsListState, NativeStateRepresentation {
	typealias SwiftType = IOSClientsListState
	
	typealias KotlinType = ClientsListState
	
	var appBar: any AppBarState
	
	var clientsList: any ListState
	
	var navigation: any NavigationState
	
	var notifications: any PresentationNotificationState
	
	var refreshState: any RefreshState
	
	var searchField: any TextFieldState
	
	init() {
		self.appBar = IOSAppBarState()
		self.clientsList = IOSListState<ClientSection>()
		self.navigation = IOSNavigationState()
		self.notifications = IOSNotificationState()
		self.refreshState = IOSRefreshState()
		self.searchField = IOSTextFieldState()
	}
	
}

extension ClientSection: @retroactive Identifiable {}
