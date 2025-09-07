//
//  PasskeyScreen.swift
//  iosApp
//
//  Created by BookkMe on 29.03.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct PasskeyScreen: View {
	
	@StateObject
	var viewModel: PasskeyViewModel = IOSSettingsDiKt.passkeyVM()
	
	var body: some View {
		VStack(spacing: 0) {
			PasskeyContent(state: viewModel.uiState, viewModel: viewModel)
		}
		.sendLifecycleEventsTo(viewModel: viewModel)
		.handleNotifications(state: viewModel.uiState.notification)
		.navigationTitle(viewModel.uiState.appBar.title.localized())
		.navigationBarTitleDisplayMode(.large)
		.toolbar {
			ToolbarItem(placement: .topBarTrailing) {
				IconButton(state: viewModel.uiState.addPasskeyButton, icon: "plus") {
					viewModel.onAddPasskeyClick()
				}
			}
		}
	}
}

private struct PasskeyContent: View {
	
	@ObservedObject
	var state: IOSPasskeyState
	
	@ObservedObject
	var viewModel: PasskeyViewModel
	
	init(state: PasskeyState, viewModel: PasskeyViewModel) {
		self.state = state.impl()
		self.viewModel = viewModel
	}
	
	var body: some View {
		Text(" ")
			.font(.footnote)//This is stupid but it works. Without this line progressView will be above navigationBarTitle an refresh animation will glitch
		List(state.passkeys, id: \.id) { passkey in
			PasskeyView(item: passkey)
				.swipeActions {
					if (passkey.isDeletable) {
						Button(DesignSystem.strings().action_delete.desc().localized()) {
							viewModel.onDeletePasskeyClick(passkeyItem: passkey)
						}
						.tint(AppColors.error)
					}
				}
		}
		.refreshable {
			viewModel.getPasskeyList()
			await state.refresh.impl().awaitRefresh()
		}
		.contentMargins(.top, 0)
		.animation(.default, value: state.passkeys)
	}
}

private struct PasskeyView: View {
	
	@State
	var item: PasskeyStatePasskeyItem
	
	var body: some View {
		HStack {
			Image(systemName: "key.fill")
			VStack(alignment: .leading) {
				Text(item.title)
					.font(.headline)
				Text(item.addedOn.localized())
					.font(.caption)
					.foregroundStyle(AppColors.secondary)
			}
		}
	}
}

#Preview {
	NavigationStack {
		PasskeyScreen()
	}
}

#Preview {
	PasskeyView(item: PasskeyStatePasskeyItem(
		id: KotlinUuid.companion.random(),
		title: "Title",
		isDeletable: true,
		isBackedUp: true,
		addedOn: RawStringDesc(string: "Added on")
	))
}
