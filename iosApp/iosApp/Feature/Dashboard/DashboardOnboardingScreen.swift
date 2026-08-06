//
//  DashboardOnboardingScreen.swift
//  iosApp
//
//  Created by BookkMe on 06.08.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct DashboardOnboardingScreen: View {

	var state: IOSOnboardingState

	init(state: OnboardingState) {
		self.state = state.impl()
	}

	var body: some View {
		ScrollView {
			VStack(alignment: .leading, spacing: 24) {
				Text(DashboardRes.strings().dashboard_onboarding_title.desc().localized())
					.font(.largeTitle)
					.fontWeight(.bold)
					.foregroundColor(AppColors.primary)

				VStack(spacing: 8) {
					Image(systemName: "storefront")
						.resizable()
						.scaledToFit()
						.frame(width: 32, height: 32)
						.foregroundColor(AppColors.primary)
						.padding(16)
						.background(AppColors.elevated)
						.clipShape(RoundedRectangle(cornerRadius: 16))

					Text(DashboardRes.strings().dashboard_onboarding_headline.desc().localized())
						.font(.title3)
						.fontWeight(.semibold)
						.foregroundColor(AppColors.primary)
						.multilineTextAlignment(.center)

					Text(DashboardRes.strings().dashboard_onboarding_subtitle.desc().localized())
						.font(.subheadline)
						.foregroundColor(AppColors.secondary)
						.multilineTextAlignment(.center)
				}
				.frame(maxWidth: .infinity)

				VStack(spacing: 0) {
					OnboardingStepRow(
						stepNumber: 1,
						isDone: state.isBusinessStepDone,
						isLocked: false,
						title: DashboardRes.strings().dashboard_onboarding_step_business_title.desc().localized(),
						subtitle: DashboardRes.strings().dashboard_onboarding_step_business_subtitle.desc().localized(),
						onClick: { state.onCreateBusinessClick?() }
					)
					Divider().padding(.horizontal, 16)
					OnboardingStepRow(
						stepNumber: 2,
						isDone: false,
						isLocked: !state.isPluginsStepUnlocked,
						title: DashboardRes.strings().dashboard_onboarding_step_plugins_title.desc().localized(),
						subtitle: DashboardRes.strings().dashboard_onboarding_step_plugins_subtitle.desc().localized(),
						onClick: { state.onEnablePluginsClick?() }
					)
				}
				.background(AppColors.elevated)
				.clipShape(RoundedRectangle(cornerRadius: 16))
			}
			.padding(.horizontal, 16)
			.padding(.vertical, 24)
		}
	}
}

private struct OnboardingStepRow: View {

	let stepNumber: Int
	let isDone: Bool
	let isLocked: Bool
	let title: String
	let subtitle: String
	let onClick: () -> Void

	private var isClickable: Bool { !isLocked && !isDone }

	var body: some View {
		Button(action: onClick) {
			HStack(spacing: 16) {
				ZStack {
					Circle()
						.fill(isLocked ? Color.clear : AppColors.actionText)
						.overlay(
							Circle().stroke(AppColors.divider, lineWidth: isLocked ? 1 : 0)
						)
						.frame(width: 32, height: 32)
					if isDone {
						Image(systemName: "checkmark")
							.font(.caption)
							.foregroundColor(AppColors.primary)
					} else {
						Text("\(stepNumber)")
							.font(.footnote)
							.foregroundColor(isLocked ? AppColors.secondary : AppColors.primary)
					}
				}

				VStack(alignment: .leading, spacing: 2) {
					Text(title)
						.font(.body)
						.fontWeight(.medium)
						.foregroundColor(isLocked ? AppColors.secondary : AppColors.primary)
					Text(subtitle)
						.font(.caption)
						.foregroundColor(AppColors.secondary)
				}

				Spacer()

				if isLocked {
					Image(systemName: "lock.fill")
						.font(.caption)
						.foregroundColor(AppColors.secondary)
				} else if isClickable {
					Image(systemName: "chevron.right")
						.font(.caption)
						.foregroundColor(AppColors.secondary)
				}
			}
			.padding(16)
			.contentShape(Rectangle())
		}
		.buttonStyle(.plain)
		.disabled(!isClickable)
	}
}

#Preview {
	DashboardOnboardingScreen(state: IOSOnboardingState())
}
