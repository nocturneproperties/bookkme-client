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
	private let retryButtonState: IOSButtonState

	init(state: ErrorState) {
		self.state = state
		self.retryButtonState = IOSButtonState(
			text: DesignSystem.strings().action_retry.desc(),
			onClick: { state.onRetryClick() }
		)
	}

	var body: some View {
		ZStack(alignment: .center) {
			VStack(alignment: .center, spacing: 20) {
				Image(systemName: "exclamationmark.triangle")
					.font(.system(size: 32))
					.foregroundStyle(AppColors.error)
				Text(state.errorText.localized())
					.font(.subheadline)
					.foregroundStyle(AppColors.secondary)
					.multilineTextAlignment(.center)
				TextButton(retryButtonState)
			}
			.padding(.horizontal, 32)
			.frame(maxWidth: .infinity, minHeight: 400, maxHeight: .infinity)
		}
	}
}
