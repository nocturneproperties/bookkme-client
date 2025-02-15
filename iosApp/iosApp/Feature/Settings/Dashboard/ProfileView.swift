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
		VStack {
			Text(state.name.localized() + " " + state.lastName.localized())
				.font(.title.weight(.bold))
			Text(state.email.localized())
				.font(.subheadline)
				.foregroundStyle(AppColors.secondary)
		}
		.frame(maxWidth: .infinity)
		.toolbar {
			NavigationLink(value: SettingsDestination.EditProfile()) {
				Text(state.editProfile.text.localized())
			}
		}
	}
}
