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
	
	@StateViewModel
	var viewModel: PasskeyViewModel = IOSSettingsDiKt.passkeyVM()
	
	var body: some View {
		PasskeyContent(state: viewModel.uiState, viewModel: viewModel)
			.sendLifecycleEventsTo(viewModel)
			.handleNotifications(viewModel.uiState.notification)
			.navigationTitle(viewModel.uiState.appBar.title.localized())
			.navigationBarTitleDisplayMode(.large)
			.background(AppColors.background)
	}
}

private struct PasskeyContent: View {
	
	let state: IOSPasskeyState
	
	let viewModel: PasskeyViewModel
	
	init(state: PasskeyState, viewModel: PasskeyViewModel) {
		self.state = state.impl()
		self.viewModel = viewModel
	}
	
	var body: some View {
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
				.toolbar {
					ToolbarItem(placement: .topBarTrailing) {
						IconButton(state: viewModel.uiState.addPasskeyButton, icon: "plus") {
							viewModel.onAddPasskeyClick()
						}
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
