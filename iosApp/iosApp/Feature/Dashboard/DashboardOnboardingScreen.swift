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
			refresh: state.refresh,
			headline: DashboardRes.strings().dashboard_onboarding_headline.desc().localized(),
			subtitle: DashboardRes.strings().dashboard_onboarding_subtitle.desc().localized()
		) {
			CreateOrJoinBusinessActions(state: state)
		}
	}
}

struct DashboardBusinessSelectionScreen: View {

	var state: IOSOnboardingState

	init(state: OnboardingState) {
		self.state = state.impl()
	}

	var body: some View {
		OnboardingLayout(
			refresh: state.refresh,
			headline: DashboardRes.strings().dashboard_onboarding_select_headline.desc().localized(),
			subtitle: DashboardRes.strings().dashboard_onboarding_select_subtitle.desc().localized()
		) {
			ForEach(state.businesses, id: \.id) { business in
				OnboardingActionRow(
					systemImage: "building.2",
					title: business.name,
					onClick: { state.onBusinessClick?(business) }
				)
			}
			Text(DashboardRes.strings().dashboard_onboarding_select_or.desc().localized())
				.font(.caption)
				.foregroundColor(AppColors.secondary)
				.frame(maxWidth: .infinity)
			CreateOrJoinBusinessActions(state: state)
		}
	}
}

private struct CreateOrJoinBusinessActions: View {

	var state: IOSOnboardingState

	var body: some View {
		VStack(spacing: 12) {
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
			refresh: state.refresh,
			headline: DashboardRes.strings().dashboard_onboarding_setup_headline.desc().localized(),
			subtitle: DashboardRes.strings().dashboard_onboarding_setup_subtitle.desc().localized()
		) {
			OnboardingActionRow(
				systemImage: "square.stack.3d.up",
				title: DashboardRes.strings().dashboard_onboarding_group_title.desc().localized(),
				subtitle: state.serviceGroupStepSubtitle.localized(),
				isDone: state.isServiceGroupStepDone,
				isEnabled: state.isServiceGroupStepEnabled,
				onClick: { state.onAddServiceGroupClick?() }
			)
			OnboardingActionRow(
				systemImage: "scissors",
				title: DashboardRes.strings().dashboard_onboarding_service_title.desc().localized(),
				subtitle: state.serviceStepSubtitle.localized(),
				isDone: state.isServiceStepDone,
				isEnabled: state.isServiceStepEnabled,
				onClick: { state.onAddServiceClick?() }
			)
			OnboardingActionRow(
				systemImage: "person.crop.circle.badge.plus",
				title: DashboardRes.strings().dashboard_onboarding_client_title.desc().localized(),
				subtitle: state.clientStepSubtitle.localized(),
				isDone: state.isClientStepDone,
				isEnabled: state.isClientStepEnabled,
				onClick: { state.onAddClientClick?() }
			)
			OnboardingActionRow(
				systemImage: "puzzlepiece.extension",
				title: DashboardRes.strings().dashboard_onboarding_plugins_title.desc().localized(),
				subtitle: DashboardRes.strings().dashboard_onboarding_plugins_subtitle.desc().localized(),
				isDone: state.isAppointmentsStepDone,
				isEnabled: state.isAppointmentsStepEnabled,
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
			refresh: state.refresh,
			headline: DashboardRes.strings().dashboard_onboarding_awaiting_headline.desc().localized(),
			subtitle: state.awaitingSetupMessage.localized()
		) {
			EmptyView()
		}
	}
}

private struct OnboardingLayout<Actions: View>: View {

	let refresh: any RefreshState
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
			.refreshable { await refresh.impl().awaitRefresh() }
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
	var isDone: Bool = false
	var isEnabled: Bool = true
	let onClick: () -> Void

	var body: some View {
		Button(action: onClick) {
			HStack(spacing: 16) {
				Image(systemName: systemImage)
					.font(.title3)
					.foregroundColor(AppColors.actionText)
					.frame(width: 32)

				if let subtitle {
					texts(subtitle: subtitle)
				} else {
					ZStack(alignment: .leading) {
						texts(subtitle: " ")
							.hidden()
						titleText
					}
				}

				Spacer()

				if isDone {
					Image(systemName: "checkmark.circle.fill")
						.font(.title3)
						.foregroundColor(AppColors.success)
				} else {
					Image(systemName: "chevron.right")
						.font(.caption)
						.foregroundColor(AppColors.secondary)
				}
			}
			.padding(16)
			.contentShape(Rectangle())
		}
		.buttonStyle(.plain)
		.disabled(isDone || !isEnabled)
		.opacity(isEnabled || isDone ? 1 : 0.5)
		.background(AppColors.elevated)
		.clipShape(RoundedRectangle(cornerRadius: 16))
	}

	private var titleText: some View {
		Text(title)
			.font(.body)
			.fontWeight(.medium)
			.foregroundColor(AppColors.primary)
	}

	private func texts(subtitle: String) -> some View {
		VStack(alignment: .leading, spacing: 2) {
			titleText
			Text(subtitle)
				.font(.caption)
				.foregroundColor(AppColors.secondary)
		}
	}
}

#Preview {
	DashboardOnboardingScreen(state: IOSOnboardingState())
}
