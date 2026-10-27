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
	
	let state: IOSProfileSection
	
	init(state: ProfileSection) {
		self.state = state.impl()
	}
	
	var body: some View {
		VStack {
			Text(state.name.localized() + " " + state.lastName.localized())
				.font(.title.weight(.bold))
				.transition(.opacity)
			Text(state.email.localized())
				.font(.subheadline)
				.foregroundStyle(AppColors.secondary)
				.transition(.opacity)
		}
		.frame(maxWidth: .infinity)
	}
}
