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
		NavigationLink(value: SettingsDestination.Contact()) {
			Text(state.contact.text.localized())
		}
		NavigationLink(value: SettingsDestination.SuggestFeature()) {
			Text(state.feature.text.localized())
		}
		NavigationLink(value: SettingsDestination.Contact()) {
			Text(state.terms.text.localized())
		}
		NavigationLink(value: SettingsDestination.Contact()) {
			Text(state.policy.text.localized())
		}
		NavigationLink(value: SettingsDestination.Report()) {
			Text(state.reportError.text.localized())
		}.foregroundStyle(AppColors.error)
	}
}
