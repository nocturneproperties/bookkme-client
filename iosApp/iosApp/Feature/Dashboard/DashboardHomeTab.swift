//
//  DashboardHomeTab.swift
//  iosApp
//
//  Created by BookkMe on 06.08.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct DashboardHomeTab: View {

	var homeState: IOSDashboardHomeState
	var onboardingNavigationStack: NavigationStackHolder

	var body: some View {
		if let content = homeState.content {
			switch content {
			case is HomeContent.NoBusiness:
				DashboardOnboardingScreen(state: homeState.onboarding)
			case is HomeContent.SetupRequired:
				DashboardSetupRequiredHost(state: homeState.onboarding, navigationStack: onboardingNavigationStack)
			case is HomeContent.AwaitingSetup:
				DashboardAwaitingSetupScreen(state: homeState.onboarding)
			default:
				AppointmentsTab()
			}
		} else {
			ProgressView()
				.frame(maxWidth: .infinity, maxHeight: .infinity)
		}
	}
}

private struct DashboardSetupRequiredHost: View {

	var state: any OnboardingState
	@ObservedObject var navigationStack: NavigationStackHolder

	var body: some View {
		NavigationStack(path: $navigationStack.path) {
			DashboardSetupRequiredScreen(state: state)
				.navigationDestination(for: DashboardHomeNavigationDestination.EnablePlugins.self) { dest in
					BusinessPluginsScreen(businessId: dest.businessId)
				}
				.navigationDestination(for: DashboardHomeNavigationDestination.AddServiceGroup.self) { _ in
					ServiceGroupListScreen()
				}
				.navigationDestination(for: DashboardHomeNavigationDestination.AddService.self) { dest in
					AddServiceScreen(businessId: dest.businessId)
				}
				.navigationDestination(for: DashboardHomeNavigationDestination.AddClient.self) { dest in
					CreateClientScreen(businessId: dest.businessId)
				}
				.navigationDestination(for: ClientsDestinations.ClientDetails.self) { dest in
					ClientDetailsScreen(id: dest.id)
				}
				.navigationDestination(for: ClientsDestinations.EditClient.self) { dest in
					EditClientScreen(id: dest.id)
				}
		}
		.environmentObject(navigationStack)
	}
}
