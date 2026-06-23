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
		List {
			SectionTextField(state.name, header: "") { newText in
				viewModel.onFirstNameTextChanged(text: newText)
			}
			.textFieldStyle(.inList)
			
			SectionTextField(state.lastName) { newText in
				viewModel.onLastNameTextChanged(text: newText)
			}
			.textFieldStyle(.inList)
			
			SectionTextField(state.email) { newText in
				viewModel.onEmailTextChanged(text: newText)
			}
			.textFieldStyle(.inList)
		}
		.listSectionSpacing(.compact)
		.sendLifecycleEventsTo(viewModel)
		.handleNotifications(state.notification)
		.navigationTitle(state.appBar.title.localized())
		.navigationBarTitleDisplayMode(.large)
		.toolbar {
			TextButton(state.confirmButton) {
				viewModel.onConfirmButtonClick()
			}
		}
	}
}

#Preview {
	EditProfileScreen()
}
