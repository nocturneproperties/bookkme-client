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
	
	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel = IOSSettingsDiKt.editProfileVM()
	
	var body: some View {
		let state = viewModel.uiState
		List {
			SectionTextField(state.name, header: "") { newText in
				viewModel.onNameTextChanged(text: newText)
			}
			.textFieldStyle(.inList)
			SectionTextField(state.phone, header: "")
				.textFieldStyle(.inList)
		}
		.listSectionSpacing(.compact)
		.scrollDismissesKeyboard(.immediately)
		.sendLifecycleEventsTo(viewModel)
		.handleNotifications(state.notification)
		.handleNavigation(state.navigation) { dest in
			switch dest {
			case is EditProfileNavigationDestination.Back:
				navigationStack.popLast()
			case is EditProfileNavigationDestination.ChangeEmail:
				navigationStack.push(SettingsDestination.ChangeEmail())
			default:
				break
			}
		}
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
