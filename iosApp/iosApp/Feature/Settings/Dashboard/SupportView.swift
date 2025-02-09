//
//  SupportView.swift
//  iosApp
//
//  Created by BookkMe on 09.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct SupportView: View {
	
	@ObservedObject
	var state: IOSSupportSection
	
	init(state: SupportSection) {
		self.state = state.impl()
	}
	
	var body: some View {
		NavigationLink(value: EditProfileDestination()) {
			Text(state.contact.text.localized())
		}
		NavigationLink(value: EditProfileDestination()) {
			Text(state.feature.text.localized())
		}
		NavigationLink(value: EditProfileDestination()) {
			Text(state.terms.text.localized())
		}
		NavigationLink(value: EditProfileDestination()) {
			Text(state.policy.text.localized())
		}
		NavigationLink(value: EditProfileDestination()) {
			Text(state.reportError.text.localized())
		}.foregroundStyle(AppColors.error)
	}
}
