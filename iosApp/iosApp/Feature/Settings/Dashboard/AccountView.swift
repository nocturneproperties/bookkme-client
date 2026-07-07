//
//  AccountView.swift
//  iosApp
//
//  Created by BookkMe on 09.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct AccountView: View {
	
	let state: IOSAccountSection
	
	var logoutClick: () -> Void
	
	
	init(state: AccountSection, logoutClick: @escaping () -> Void) {
		self.state = state.impl()
		self.logoutClick = logoutClick
	}
	
	var body: some View {
		NavigationLink(value: SettingsDestination.Notifications()) {
			Text(state.notifications.text.localized())
		}
		NavigationLink(value: SettingsDestination.Passkey()) {
			Text(state.passkey.text.localized())
		}
		Button {
			logoutClick()
		} label: {
			Text(state.logout.text.localized())
		}
		NavigationLink(value: SettingsDestination.DeleteAccount()) {
			Text(state.deleteAccount.text.localized())
		}.foregroundStyle(AppColors.error)
	}
}
