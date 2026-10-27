//
//  DashboardScreen.swift
//  iosApp
//
//  Created by BookkMe on 05.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct DashboardScreen: View {

	@StateViewModel
	var dashboardVM = IOSDashboardDiKt.dashboardVM()

	var body: some View {
		DashboardTabs(state: dashboardVM.uiState.impl())
	}
}

struct DashboardTabs: View {

	@Bindable
	var state: IOSDashboardState

	@StateObject private var onboardingNavigationStack = NavigationStackHolder()

	var body: some View {
		tabView
			.handleNavigation(state.navigation, handler: handleNavigation)
			.handleNotifications(state.notifications)
	}

	private func handleNavigation(_ destination: NavigationDestination) {
		switch destination {
		case let destination as DashboardHomeNavigationDestination.EnablePlugins:
			onboardingNavigationStack.push(destination)
		default:
			break
		}
	}

	private var tabView: some View {
		TabView(selection: Binding(
			get: { state.tabItems.selectedItemId },
			set: { state.tabItems.selectedItemId = $0 }
		)) {
			ForEach(state.tabItems.items, id: \.id) { tabItem in
				let item = tabItem.impl()
				Tab(value: item.id) {
					DashboardTabContent(id: item.id, dashboardState: state, onboardingNavigationStack: onboardingNavigationStack)
				} label: {
					Label(item.text.localized(), systemImage: iconFrom(id: item.id))
				}
				.badge(item.badgeText.map { Text($0.localized()) })
				.disabled(!item.isEnabled)
			}
		}
	}
}

struct DashboardTabContent: View {

	var id: TabItemId
	var dashboardState: IOSDashboardState
	var onboardingNavigationStack: NavigationStackHolder

	var body: some View {
		switch id {
		case .home:
			DashboardHomeTab(homeState: dashboardState.home.impl(), onboardingNavigationStack: onboardingNavigationStack)
		case .business:
			BusinessTab(isEnabled: dashboardState.tabItems.items.first { $0.id == .business }?.isEnabled ?? false)
		case .settings:
			SettingsTab()
		default:
			fatalError("Unsupported tab \(id)")
		}
	}
}

private func iconFrom(id: TabItemId) -> String {
	return switch id {
	case TabItemId.home:
		"house.fill"
	case TabItemId.business:
		"point.3.connected.trianglepath.dotted"
	case TabItemId.settings:
		"gear"
	default:
		"ellipsis.curlybraces"
	}
}

#Preview {
	DashboardScreen()
}
