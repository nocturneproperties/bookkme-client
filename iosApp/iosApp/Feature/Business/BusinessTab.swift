//
//  BusinessTab.swift
//  iosApp
//
//  Created by BookkMe on 09.05.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct BusinessTab: View {
	@StateObject var navigationStack = NavigationStackHolder()
	@StateObject var bootstrapVM = IOSBusinessDiKt.businessBootstrapVM()
	
	var body: some View {
		NavigationStack(path: $navigationStack.path) {
			BusinessStartDestinationView(state: bootstrapVM.uiState)
				.handleNotifications(state: bootstrapVM.uiState.notification)
		}
	}
}

struct BusinessStartDestinationView: View {
	
	@ObservedObject
	var state: IOSBusinessBootstrapState
	
	init(state: BusinessBootstrapState) {
		self.state = state.impl()
	}
	
	var body: some View {
		ZStack {
			switch state.startDestination {
			case is BusinessDestination.BlockingProgress:
				ProgressView()
			case is BusinessDestination.Create:
				CreateBusinessScreen()
			default:
				BusinessDashboardScreen()
			}
		}.animation(.default, value: state.startDestination)
	}
}
