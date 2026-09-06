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
			case is HomeContent.Onboarding:
				DashboardOnboardingHost(state: homeState.onboarding, navigationStack: onboardingNavigationStack)
			default:
				AppointmentsTab()
			}
		} else {
			EmptyView()
		}
	}
}

private struct DashboardOnboardingHost: View {

	var state: any OnboardingState
	@ObservedObject var navigationStack: NavigationStackHolder

	var body: some View {
		NavigationStack(path: $navigationStack.path) {
			DashboardOnboardingScreen(state: state)
				.navigationDestination(for: DashboardHomeNavigationDestination.EnablePlugins.self) { dest in
					BusinessPluginsScreen(businessId: dest.businessId)
				}
		}
		.environmentObject(navigationStack)
	}
}
