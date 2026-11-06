//
//  SettingsDashboard.swift
//  iosApp
//
//  Created by BookkMe on 07.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct SettingsDashboardScreen: View {
	
	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var settingsVM = IOSSettingsDiKt.settingsVM()
	
	var body: some View {
		let uiState = settingsVM.uiState
		VStack {
			List {
				ProfileView(state: uiState.profile)
					.listRowBackground(AppColors.elevated)
				Section(uiState.appearance.title.localized()) {
					AppearanceView(state: uiState.appearance) { scheme in
						settingsVM.onSchemeSelected(scheme: scheme)
					}
				}
				Section(uiState.account.title.localized()) {
					AccountView(state: uiState.account) {
						settingsVM.onLogOutClick()
					}
				}
				Section(uiState.support.title.localized()) {
					SupportView(state: uiState.support)
				}
			}
		}
		.scrollDismissesKeyboard(.immediately)
		.withNavigationBar(uiState.appBar)
		.sendLifecycleEventsTo(settingsVM)
		.handleNotifications(uiState.notification)
		.handleNavigation(uiState.navigation) { dest in
			switch dest {
			case is SettingsDashboardDestination.EditProfile:
				navigationStack.push(SettingsDestination.EditProfile())
			default:
				break
			}
		}
	}
}

#Preview {
	SettingsDashboardScreen()
}
