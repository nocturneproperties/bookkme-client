//
//  CreateBusinessScreen.swift
//  iosApp
//
//  Created by BookkMe on 09.05.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct CreateBusinessScreen: View {
	
	@StateViewModel var viewModel = IOSBusinessDiKt.createBusinessVM()
	
	var body: some View {
		let uiState = viewModel.uiState
		VStack {
			StateTextField(state: uiState.name.impl()) { text in
				viewModel.onBusinessNameChanged(name: text)
			}
			.textContentType(.organizationName)
			.submitLabel(.done)
			Spacer()
		}
		.padding()
		.navigationTitle(uiState.appBar.title.localized())
		.navigationBarTitleDisplayMode(.large)
		.toolbar {
			TextButton(state: uiState.createBtn) {
				viewModel.onCreateClick()
			}
		}
		.handleNotifications(state: uiState.notifications)
		.sendLifecycleEventsTo(viewModel: viewModel)
	}
}
