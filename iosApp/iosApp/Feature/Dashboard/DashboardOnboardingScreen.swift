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
		OnboardingLayout(
			headline: DashboardRes.strings().dashboard_onboarding_headline.desc().localized(),
			subtitle: DashboardRes.strings().dashboard_onboarding_subtitle.desc().localized()
		) {
			OnboardingActionRow(
				systemImage: "storefront",
				title: DashboardRes.strings().dashboard_onboarding_create_title.desc().localized(),
				subtitle: DashboardRes.strings().dashboard_onboarding_create_subtitle.desc().localized(),
				onClick: { state.onCreateBusinessClick?() }
			)
			OnboardingActionRow(
				systemImage: "person.badge.plus",
				title: DashboardRes.strings().dashboard_onboarding_join_title.desc().localized(),
				subtitle: DashboardRes.strings().dashboard_onboarding_join_subtitle.desc().localized(),
				onClick: { state.onJoinBusinessClick?() }
			)
			if !state.businesses.isEmpty {
				Text(DashboardRes.strings().dashboard_onboarding_select_title.desc().localized())
					.font(.subheadline)
					.foregroundColor(AppColors.secondary)
					.multilineTextAlignment(.center)
					.frame(maxWidth: .infinity)
					.padding(.top, 12)
				ForEach(state.businesses, id: \.id) { business in
					OnboardingActionRow(
						systemImage: "building.2",
						title: business.name,
						onClick: { state.onBusinessClick?(business) }
					)
				}
			}
		}
	}
}

struct DashboardSetupRequiredScreen: View {

	var state: IOSOnboardingState

	init(state: OnboardingState) {
		self.state = state.impl()
	}

	var body: some View {
		OnboardingLayout(
			headline: DashboardRes.strings().dashboard_onboarding_setup_headline.desc().localized(),
			subtitle: DashboardRes.strings().dashboard_onboarding_setup_subtitle.desc().localized()
		) {
			OnboardingActionRow(
				systemImage: "puzzlepiece.extension",
				title: DashboardRes.strings().dashboard_onboarding_plugins_title.desc().localized(),
				subtitle: DashboardRes.strings().dashboard_onboarding_plugins_subtitle.desc().localized(),
				onClick: { state.onEnablePluginsClick?() }
			)
		}
	}
}

struct DashboardAwaitingSetupScreen: View {

	var state: IOSOnboardingState

	init(state: OnboardingState) {
		self.state = state.impl()
	}

	var body: some View {
		OnboardingLayout(
			headline: DashboardRes.strings().dashboard_onboarding_awaiting_headline.desc().localized(),
			subtitle: state.awaitingSetupMessage.localized()
		) {
			EmptyView()
		}
	}
}

private struct OnboardingLayout<Actions: View>: View {

	let headline: String
	let subtitle: String
	@ViewBuilder let actions: () -> Actions

	var body: some View {
		GeometryReader { geometry in
			ScrollView {
				VStack(spacing: 0) {
					Spacer(minLength: 0)
					content
					Spacer(minLength: 0)
					Spacer(minLength: 0)
				}
				.frame(minHeight: max(geometry.size.height - verticalPadding * 2, 0))
				.padding(.horizontal, 16)
				.padding(.vertical, verticalPadding)
			}
		}
	}

	private let verticalPadding: CGFloat = 24

	private var content: some View {
		VStack(alignment: .leading, spacing: 24) {
			VStack(spacing: 8) {
				Text(headline)
					.font(.title3)
					.fontWeight(.semibold)
					.foregroundColor(AppColors.primary)
					.multilineTextAlignment(.center)

				Text(subtitle)
					.font(.subheadline)
					.foregroundColor(AppColors.secondary)
					.multilineTextAlignment(.center)
			}
			.frame(maxWidth: .infinity)

			VStack(spacing: 12) {
				actions()
			}
		}
	}
}

private struct OnboardingActionRow: View {

	let systemImage: String
	let title: String
	var subtitle: String? = nil
	let onClick: () -> Void

	var body: some View {
		Button(action: onClick) {
			HStack(spacing: 16) {
				Image(systemName: systemImage)
					.font(.title3)
					.foregroundColor(AppColors.actionText)
					.frame(width: 32)

				VStack(alignment: .leading, spacing: 2) {
					Text(title)
						.font(.body)
						.fontWeight(.medium)
						.foregroundColor(AppColors.primary)
					if let subtitle {
						Text(subtitle)
							.font(.caption)
							.foregroundColor(AppColors.secondary)
					}
				}

				Spacer()

				Image(systemName: "chevron.right")
					.font(.caption)
					.foregroundColor(AppColors.secondary)
			}
			.padding(16)
			.contentShape(Rectangle())
		}
		.buttonStyle(.plain)
		.background(AppColors.elevated)
		.clipShape(RoundedRectangle(cornerRadius: 16))
	}
}

#Preview {
	DashboardOnboardingScreen(state: IOSOnboardingState())
}
