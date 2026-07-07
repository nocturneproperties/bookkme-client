//
//  SettingsTab.swift
//  iosApp
//
//  Created by BookkMe on 14.06.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//
import SwiftUI
import shared

struct SettingsTab: View {
	@StateObject var navigationStack = NavigationStackHolder()
	
	var body: some View {
		NavigationStack(path: $navigationStack.path) {
			SettingsDashboardScreen()
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
				.navigationDestination(for: SettingsDestination.Notifications.self) { _ in
					NotificationSettingsScreen()
				}
		}.environmentObject(navigationStack)
	}
}
