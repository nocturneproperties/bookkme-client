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
				.navigationDestination(for: SettingsDestination.PersonalDataExport.self) { _ in
					PersonalDataExportScreen()
				}
				.navigationDestination(for: SettingsDestination.EditProfile.self) { _ in
					EditProfileScreen()
				}
				.navigationDestination(for: EditProfileNavigationDestination.ChangeEmail.self) { _ in
					ChangeEmailScreen()
				}
				.navigationDestination(for: ChangeEmailDestinations.VerifyEmail.self) { value in
					VerifyEmailScreen(pendingEmailChange: value.pendingEmailChange)
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
				.navigationDestination(for: SettingsDestination.Privacy.self) { _ in
					PrivacySettingsScreen()
				}
		}.environmentObject(navigationStack)
	}
}
