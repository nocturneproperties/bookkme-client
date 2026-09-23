//
//  ListErrorView.swift
//  iosApp
//
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct ListErrorView: View {

	var state: ErrorState

	var body: some View {
		ContentUnavailableView {
			Label {
				Text(state.title.localized())
			} icon: {
				Image(systemName: "wifi.slash")
					.foregroundStyle(AppColors.secondary)
			}
		} description: {
			Text(state.subtitle.localized())
		} actions: {
			Button(DesignSystem.strings().action_retry.desc().localized()) {
				state.onRetryClick()
			}
			.buttonStyle(.textAction)
		}
	}
}
