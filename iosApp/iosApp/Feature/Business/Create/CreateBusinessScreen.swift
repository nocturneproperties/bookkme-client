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
			StateTextField(uiState.name.impl()) { text in
				viewModel.onBusinessNameChanged(name: text)
			}
			.textContentType(.organizationName)
			.submitLabel(.done)
			.textFieldStyle(.standalone)
			Spacer()
		}
		.padding()
		.navigationTitle(uiState.appBar.title.localized())
		.navigationBarTitleDisplayMode(.large)
		.toolbar {
			TextButton(uiState.createBtn) {
				viewModel.onCreateClick()
			}
		}
		.handleNotifications(uiState.notifications)
		.sendLifecycleEventsTo(viewModel)
		.background(AppColors.background)
	}
}
