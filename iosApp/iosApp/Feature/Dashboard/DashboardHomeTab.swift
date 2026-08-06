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
	var navigation: any NavigationState

	var body: some View {
		switch homeState.content {
		case is HomeContent.Loading:
			ProgressView()
		case is HomeContent.Onboarding:
			DashboardOnboardingHost(state: homeState.onboarding, navigation: navigation)
		default:
			AppointmentsTab()
		}
	}
}

private struct DashboardOnboardingHost: View {

	@StateObject var navigationStack = NavigationStackHolder()
	@State private var isCreateBusinessSheetPresented = false
	var state: any OnboardingState
	var navigation: any NavigationState

	var body: some View {
		NavigationStack(path: $navigationStack.path) {
			DashboardOnboardingScreen(state: state)
				.handleNavigation(navigation) { dest in
					switch dest {
					case is DashboardHomeNavigationDestination.CreateBusiness:
						isCreateBusinessSheetPresented = true
					case let dest as DashboardHomeNavigationDestination.EnablePlugins:
						navigationStack.push(dest)
					default:
						break
					}
				}
				.navigationDestination(for: DashboardHomeNavigationDestination.EnablePlugins.self) { dest in
					BusinessPluginsScreen(businessId: dest.businessId)
				}
				.sheet(isPresented: $isCreateBusinessSheetPresented) {
					NavigationStack {
						CreateBusinessScreen()
					}
				}
		}
		.environmentObject(navigationStack)
	}
}
