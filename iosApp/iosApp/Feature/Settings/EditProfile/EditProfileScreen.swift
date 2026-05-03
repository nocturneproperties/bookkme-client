//
//  EditProfileView.swift
//  iosApp
//
//  Created by BookkMe on 16.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct EditProfileScreen: View {
	
	@StateObject var navigationStack = NavigationStackHolder()
	@StateViewModel var viewModel = IOSSettingsDiKt.editProfileVM()
	
	var body: some View {
		let state = viewModel.uiState
		VStack {
			StateTextField(state: state.name) { newText in
				viewModel.onFirstNameTextChanged(text: newText)
			}
			StateTextField(state: state.lastName) { newText in
				viewModel.onLastNameTextChanged(text: newText)
			}
			StateTextField(state: state.email) { newText in
				viewModel.onEmailTextChanged(text: newText)
			}
			Spacer()
		}
		.sendLifecycleEventsTo(viewModel)
		.handleNotifications(state.notification)
		.padding()
		.background(AppColors.background)
		.navigationTitle(state.appBar.title.localized())
		.navigationBarTitleDisplayMode(.large)
		.toolbar {
			TextButton(state: state.confirmButton) {
				viewModel.onConfirmButtonClick()
			}
		}
	}
}

#Preview {
	EditProfileScreen()
}
