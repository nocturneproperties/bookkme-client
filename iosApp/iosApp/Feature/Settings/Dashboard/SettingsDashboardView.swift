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
				Section(uiState.appearance.title.localized()) {
					AppearanceView(state: uiState.appearance) { scheme in
						settingsVM.onSchemeSelected(scheme: scheme)
					}
				}
				Section(uiState.profile.title.localized()) {
					ProfileView(state: uiState.profile)
				}
				Section(uiState.account.title.localized()) {
					AccountView(state: uiState.account)
				}
				Section(uiState.support.title.localized()) {
					SupportView(state: uiState.support)
				}
			}
			.navigationDestination(for: EditProfileDestination.self) { _ in
				
			}
			.navigationDestination(for: PasskeyDestination.self) { _ in
				
			}
			.navigationDestination(for: DeleteAccountDestination.self) { _ in
				
			}
			.navigationDestination(for: ContactDestination.self) { _ in
				
			}
			.navigationDestination(for: SuggestFeatureDestination.self) { _ in
				
			}
			.navigationDestination(for: ReportDestination.self) { _ in
				
			}
		}
	}
}

#Preview {
	SettingsDashboardView()
}
