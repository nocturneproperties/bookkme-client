//
//  SettingsDashboard.swift
//  iosApp
//
//  Created by BookkMe on 07.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct SettingsDashboardView: View {
	
	@StateObject var navigationStack = NavigationStackHolder()
	@StateObject var settingsVM = IOSSettingsDiKt.settingsVM()
	
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
					AccountView(state: uiState.account)
				}
				Section(uiState.support.title.localized()) {
					SupportView(state: uiState.support)
				}
			}
			.navigationDestination(for: SettingsDestination.EditProfile.self) { _ in
				EditProfileView()
			}
			.navigationDestination(for: SettingsDestination.Passkey.self) { _ in
				
			}
			.navigationDestination(for: SettingsDestination.DeleteAccount.self) { _ in
				
			}
			.navigationDestination(for: SettingsDestination.Contact.self) { _ in
				
			}
			.navigationDestination(for: SettingsDestination.SuggestFeature.self) { _ in
				
			}
			.navigationDestination(for: SettingsDestination.Report.self) { _ in
				
			}
		}.environmentObject(navigationStack)
	}
}

#Preview {
	SettingsDashboardView()
}
