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
	@StateViewModel var bootstrapVM = IOSBusinessDiKt.businessBootstrapVM()
	
	var body: some View {
		NavigationStack(path: $navigationStack.path) {
			BusinessStartDestinationView(state: bootstrapVM.uiState)
				.handleNotifications(bootstrapVM.uiState.notification)
				.navigationDestination(for: ClientsDestinations.ClientDetails.self) { type in
					CreateClientScreen(businessId: type.id)
				}
				.navigationDestination(for: ClientsDestinations.CreateClient.self) { type in
					CreateClientScreen(businessId: type.businessId)
				}
				.navigationDestination(for: DashboardNavigationDestination.self) { type in
					switch type {
					case let type as DashboardNavigationDestination.Settings:
						BusinessSettingsScreen(id: type.id)
					case let type as DashboardNavigationDestination.Clients:
						ClientsListScreen(businessId: type.id)
					default:
						ProgressView()
					}
				}
		}.environmentObject(navigationStack)
	}
}

struct BusinessStartDestinationView: View {
	
	let state: IOSBusinessBootstrapState
	
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
