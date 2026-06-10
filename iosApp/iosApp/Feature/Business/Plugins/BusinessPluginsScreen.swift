//
//  BusinessPluginsScreen.swift
//  iosApp
//
//  Created by BookkMe on 10.06.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct BusinessPluginsScreen: View {
	
	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: BusinessPluginsViewModel
	
	init(businessId: KotlinUuid) {
		self._viewModel = StateViewModel(wrappedValue: IOSBusinessDiKt.businessPluginsVM(id: businessId))
	}
	
	var body: some View {
		ScrollView {
			VStack(spacing: 16) {
				BusinessPluginCard(state: viewModel.uiState.appointmentPlugin)
			}
			.padding(.vertical, 24)
			.padding(.horizontal, 16)
			.withNavigationBar(viewModel.uiState.appBar)
			.sendLifecycleEventsTo(viewModel)
			.handleNotifications(viewModel.uiState.notifications)
			.handleNavigation(viewModel.uiState.navigation) { dest in
				switch dest {
				case is BusinessPluginsDestinations.Back:
					navigationStack.popLast()
				default:
					break
				}
			}
		}
	}
}

struct BusinessPluginCard: View {
	
	var state: IOSBusinessPluginState
	
	init(state: BusinessPluginState) {
		self.state = IOSBusinessPluginState.cast(state)
	}
	
	private var headerRow: some View {
		HStack(alignment: .center, spacing: 0) {
			Image(systemName: "calendar")
				.resizable()
				.scaledToFit()
				.foregroundColor(AppColors.actionText)
				.padding(8)
				.frame(width: 54, height: 54)
				.background(AppColors.actionText.opacity(0.1))
				.clipShape(RoundedRectangle(cornerRadius: 8))
			
			VStack(alignment: .leading, spacing: 2) {
				Text(state.title.localized())
					.font(.title3)
					.fontWeight(.medium)
					.foregroundColor(.primary)
				Text(state.subtitle.localized())
					.font(.caption)
					.foregroundColor(.secondary)
			}
			.padding(.leading, 16)
			
			Spacer()
			
			Text(state.isEnabled ? BusinessRes.strings().business_plugin_enabled.desc().localized() : BusinessRes.strings().business_plugin_disabled.desc().localized())
				.font(.caption2)
				.padding(.horizontal, 8)
				.padding(.vertical, 2)
				.background(
					state.isEnabled
					? AppColors.actionText
					: AppColors.primary.opacity(0.1)
				)
				.clipShape(RoundedRectangle(cornerRadius: 8))
				.lineLimit(1)
		}
	}
	
	var body: some View {
		Button {
			withAnimation(.easeInOut(duration: 0.25)) {
				state.isExpanded.toggle()
			}
		} label: {
			VStack(alignment: .leading, spacing: 0) {
				headerRow
				if state.isExpanded {
					expandedContent
				}
			}
			.padding(16)
		}
		.buttonStyle(.plain)
		.contentShape(Rectangle())
		.background(AppColors.elevated)
		.clipShape(RoundedRectangle(cornerRadius: 12))
	}
	
	private var expandedContent: some View {
		VStack(alignment: .leading, spacing: 8) {
			FeatureListView(
				icon: "person",
				title: BusinessRes.strings().business_plugins_you_can.desc().localized(),
				features: state.youCan.items(OptionalInfoLine.self)
			)
			.padding(.top, 16)
			
			FeatureListView(
				icon: "person.2",
				title: BusinessRes.strings().business_plugins_clients_can.desc().localized(),
				features: state.clientCan.items(OptionalInfoLine.self)
			)
			
			if !state.isEnabled {
				if let demo = state.demo {
					Button(action: demo.onClick) {
						Text(demo.title.localized())
							.font(.caption)
							.foregroundColor(AppColors.actionText)
							.frame(maxWidth: .infinity)
							.padding(.horizontal, 16)
							.padding(.vertical, 8)
					}
					.background(AppColors.primary.opacity(0.05))
					.clipShape(RoundedRectangle(cornerRadius: 8))
				}
				
				StateButton(state.enable)
			}
		}
	}
}

struct FeatureListView: View {
	let icon: String
	let title: String
	let features: [OptionalInfoLine]
	
	var body: some View {
		VStack(alignment: .leading, spacing: 0) {
			HStack(spacing: 8) {
				Image(systemName: icon)
					.foregroundColor(AppColors.actionText)
				Text(title)
					.font(.body)
					.fontWeight(.medium)
					.foregroundColor(.primary)
			}
			
			Divider()
				.padding(.vertical, 8)
			
			ForEach(features, id: \.self) { item in
				HStack(alignment: .top, spacing: 8) {
					Image(systemName: "checkmark.circle")
						.resizable()
						.scaledToFit()
						.frame(width: 18, height: 18)
						.foregroundColor(AppColors.actionText)
					
					VStack(alignment: .leading, spacing: 2) {
						Text(item.title.localized())
							.font(.caption)
							.foregroundColor(.primary)
						if let subtitle = item.value {
							Text(subtitle.localized())
								.font(.caption2)
								.foregroundColor(.secondary)
						}
					}
					
					Spacer()
				}
				.padding(.vertical, 4)
				.padding(.horizontal, 2)
			}
		}
		.padding(8)
		.background(AppColors.primary.opacity(0.05))
		.clipShape(RoundedRectangle(cornerRadius: 8))
	}
}
