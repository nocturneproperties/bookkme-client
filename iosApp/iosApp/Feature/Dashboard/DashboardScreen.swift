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

	var body: some View {
		TabView(selection: Binding(
			get: { state.tabItems.selectedItemId },
			set: { newId in
				if state.tabItems.items.first(where: { $0.id == newId })?.isEnabled == true {
					state.tabItems.selectedItemId = newId
				}
			}
		)) {
			ForEach(state.tabItems.items, id:\.id) { item in
				DashboardTab(item: item.impl(), dashboardState: state)
			}
		}
	}
}

struct DashboardTab: View {

	var item: IOSTabItem
	var dashboardState: IOSDashboardState

	var body: some View {
		screenFromId(id: item.id)
			.tabItem {
				Label(
					title: {
						Text(item.text.localized())
					},
					icon: {
						Image(systemName: iconFrom(id: item.id))
					}
				)
				.opacity(item.isEnabled ? 1 : 0.4)
			}
			.badge(item.badgeText?.localized())
	}

	@ViewBuilder
	private func screenFromId(id: TabItemId) -> some View {
		switch id {
		case .home:
			DashboardHomeTab(homeState: dashboardState.home.impl(), navigation: dashboardState.navigation)
		case .business:
			BusinessTab()
		case .settings:
			SettingsTab()
		default:
			fatalError("Unsupported tab \(item.id)")
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

