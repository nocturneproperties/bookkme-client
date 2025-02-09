//
//  ProfileView.swift
//  iosApp
//
//  Created by BookkMe on 09.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//
import SwiftUI
import shared

struct ProfileView: View {
	
	@ObservedObject
	var state: IOSProfileSection
	
	init(state: ProfileSection) {
		self.state = state.impl()
	}
	
	var body: some View {
		HStack {
			Text(SettingsRes.strings().settings_profile_name.desc().localized())
				.frame(minWidth: 100, alignment: .leading)
			Text(state.name.localized())
		}
		HStack {
			Text(SettingsRes.strings().settings_profile_last_name.desc().localized())
				.frame(minWidth: 100, alignment: .leading)
			Text(state.lastName.localized())
		}
		HStack {
			Text(SettingsRes.strings().settings_profile_email.desc().localized())
				.frame(minWidth: 100, alignment: .leading)
			Text(state.email.localized())
		}
		NavigationLink(value: EditProfileDestination()) {
			Text(state.editProfile.text.localized())
		}.foregroundStyle(AppColors.actionText)
	}
}
