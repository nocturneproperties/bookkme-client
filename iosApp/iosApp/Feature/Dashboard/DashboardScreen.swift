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
	
	@StateObject
	var dashboardVM = IOSDashboardDiKt.dashboardVM()
	
	var body: some View {
		DashboardTabs(tabsState: dashboardVM.uiState.tabItems.impl())
	}
}
struct DashboardTabs: View {
	
	@ObservedObject
	var tabsState: IOSTabItemsState
	
	var body: some View {
		TabView(selection: $tabsState.selectedItemId) {
			ForEach(tabsState.items, id:\.id) { state in
				DashboardTab(state: state.impl())
			}
		}
	}
}

struct DashboardTab: View {

	@ObservedObject
	var state: IOSTabItem
	
	var body: some View {
		Text(state.text.localized())
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

}

private func iconFrom(id: TabItemId) -> String {
	return switch id {
	case TabItemId.appointments:
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

