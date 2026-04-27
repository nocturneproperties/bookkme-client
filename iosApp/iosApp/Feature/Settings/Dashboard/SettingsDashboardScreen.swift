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
	
	@StateObject var navigationStack = NavigationStackHolder()
	@StateViewModel var settingsVM = IOSSettingsDiKt.settingsVM()
	
	var body: some View {
		let uiState = settingsVM.uiState
		NavigationStack(path: $navigationStack.path) {
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
			.navigationDestination(for: SettingsDestination.EditProfile.self) { _ in
				EditProfileScreen()
			}
			.navigationDestination(for: SettingsDestination.Passkey.self) { _ in
				PasskeyScreen()
			}
			.navigationDestination(for: SettingsDestination.DeleteAccount.self) { _ in
				DeleteAccountScreen()
			}
			.navigationDestination(for: SettingsDestination.ContactUs.self) { _ in
				ContactUsScreen()
			}
		}
		.sendLifecycleEventsTo(viewModel: settingsVM)
		.handleNotifications(state: uiState.notification)
		.environmentObject(navigationStack)
	}
}

#Preview {
	SettingsDashboardScreen()
}
