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
		DashboardTabs(tabsState: dashboardVM.uiState.tabItems.impl())
	}
}
struct DashboardTabs: View {
	
	@Bindable
	var tabsState: IOSTabItemsState
	
	var body: some View {
		TabView(selection: Binding(
			get: { tabsState.selectedItemId },
			set: { tabsState.selectedItemId = $0 }
		)) {
			ForEach(tabsState.items, id:\.id) { state in
				DashboardTab(state: state.impl())
			}
		}
	}
}

struct DashboardTab: View {

	var state: IOSTabItem
	
	var body: some View {
		screenFromId(id: state.id)
			.tabItem {
				Label(
					title: {
						Text(state.text.localized())
					},
					icon: {
						Image(systemName: iconFrom(id: state.id))
					}
				)
			}.badge(state.badgeText?.localized())
	}
	
	@ViewBuilder
	private func screenFromId(id: TabItemId) -> some View {
		switch id {
		case .home:
			AppointmentsTab()
		case .business:
			BusinessTab()
		case .settings:
			SettingsDashboardScreen()
		default:
			fatalError("Unsupported tab \(state.id)")
		}
	}

}

private func iconFrom(id: TabItemId) -> String {
	return switch id {
	case TabItemId.home:
		"calendar.day.timeline.left"
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

