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
		let state = viewModel.uiState.impl()
		let businessMenu = state.businessMenu.impl()
		VStack {
			BusinessDashboardScreenContent(state: viewModel.uiState)
		}
		.withNavigationBar(viewModel.uiState.appBar)
		.toolbar {
			ToolbarItem(placement: .topBarTrailing) {
				Menu {
					businessMenuContent(businessMenu)
				} label: {
					Image(systemName: "building.2")
				}
				.accessibilityLabel(BusinessRes.strings().business_dashboard_switch_action.desc().localized())
			}
		}
		.sheet(isPresented: Binding(
			get: { state.isCreateBusinessSheetVisible },
			set: { state.isCreateBusinessSheetVisible = $0 }
		)) {
			NavigationStack {
				CreateBusinessScreen()
			}
		}
		.handleNotifications(viewModel.uiState.notifications)
		.sendLifecycleEventsTo(viewModel)
		.handleNavigation(viewModel.uiState.navigation) { destination in
		}
	}

	@ViewBuilder
	private func businessMenuContent(_ businessMenu: IOSBusinessMenuState) -> some View {
		ForEach(businessMenu.items, id: \.id) { business in
			Button {
				businessMenu.onBusinessClick?(business.id)
			} label: {
				if business.id == businessMenu.selectedBusinessId {
					Label(business.name, systemImage: "checkmark")
				} else {
					Text(business.name)
				}
			}
		}
		Divider()
		Button {
			businessMenu.onCreateClick?()
		} label: {
			Label(BusinessRes.strings().business_dashboard_switch_create.desc().localized(), systemImage: "plus")
		}
		Button {
			businessMenu.onJoinClick?()
		} label: {
			Label(BusinessRes.strings().business_dashboard_switch_join.desc().localized(), systemImage: "person.badge.plus")
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
