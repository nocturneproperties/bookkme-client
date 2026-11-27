//
//  AccountDeletionBanner.swift
//  iosApp
//
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct AccountDeletionBanner: View {

	var state: any AccountDeletionBannerState

	var body: some View {
		VStack(spacing: 0) {
			if state.isVisible {
				AlertStrip(
					text: state.message,
					action: state.cancelButton,
					icon: DesignSystem.images().material_auto_delete
				)
				.transition(.move(edge: .bottom).combined(with: .opacity))
			}
		}
		.animation(.default, value: state.isVisible)
	}
}

private struct AccountDeletionBannerEvents: ViewModifier {

	var viewModel: AccountDeletionBannerViewModel

	@Environment(\.scenePhase)
	private var scenePhase

	func body(content: Content) -> some View {
		content
			.onAppear { viewModel.onAppResumed() }
			.onChange(of: scenePhase) { _, phase in
				if phase == .active {
					viewModel.onAppResumed()
				}
			}
			.handleNotifications(viewModel.uiState.notifications)
	}
}

extension View {
	func sendAccountDeletionEventsTo(_ viewModel: AccountDeletionBannerViewModel) -> some View {
		modifier(AccountDeletionBannerEvents(viewModel: viewModel))
	}
}
