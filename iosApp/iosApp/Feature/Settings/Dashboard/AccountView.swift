//
//  AccountView.swift
//  iosApp
//
//  Created by BookkMe on 09.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct AccountView:View {
	
	@ObservedObject
	var state: IOSAccountSection
	
	init(state: AccountSection) {
		self.state = state.impl()
	}
	
	var body: some View {
		NavigationLink(value: SettingsDestination.Passkey()) {
			Text(state.passkey.text.localized())
		}
		Text(state.logout.text.localized())
		NavigationLink(value: SettingsDestination.DeleteAccount()) {
			Text(state.deleteAccount.text.localized())
		}.foregroundStyle(AppColors.error)
	}
}
