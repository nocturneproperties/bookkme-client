//
//  BusinessDashboardScreen.swift
//  iosApp
//
//  Created by BookkMe on 20.05.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct BusinessDashboardScreen: View {
	
	@StateViewModel var viewModel: BusinessDashboardViewModel = IOSBusinessDiKt.businessDashboardVM()
	
	var body: some View {
		VStack {
			BusinessDashboardScreenContent(state: viewModel.uiState)
		}
		.withNavigationBar(viewModel.uiState.appBar)
		.handleNotifications(viewModel.uiState.notifications)
		.sendLifecycleEventsTo(viewModel)
		.handleNavigation(viewModel.uiState.navigation) { navigation in
		}
	}
}

struct BusinessDashboardScreenContent: View {
	
	let state: IOSBusinessDashboardState
	
	init(state: BusinessDashboardState) {
		self.state = state.impl()
	}
	
	var body: some View {
		List {
			ForEach(state.sections, id: \.self) { sectionModel in
				Section(sectionModel.title.localized()) {
					ForEach(sectionModel.items, id: \.self) { itemModel in
						NavigationLink(value: itemModel.navigation) {
							Text(itemModel.text.localized())
						}
					}
				}
			}
			.scrollDismissesKeyboard(.immediately)
		}
	}
}

#Preview {
	BusinessDashboardScreen()
}
