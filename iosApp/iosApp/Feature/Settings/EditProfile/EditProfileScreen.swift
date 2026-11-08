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
				viewModel.onFirstNameTextChanged(text: newText)
			}
			.textFieldStyle(.inList)
			
			SectionTextField(state.lastName) { newText in
				viewModel.onLastNameTextChanged(text: newText)
			}
			.textFieldStyle(.inList)
			
			SectionTextField(state.email)
			.textFieldStyle(.inList)

			Section {
				TextButton(state.changeEmailButton)
					.listRowBackground(Color.clear)
					.listRowInsets(EdgeInsets())
			}
			.listRowSeparator(.hidden)
		}
		.listSectionSpacing(.compact)
		.scrollDismissesKeyboard(.immediately)
		.sendLifecycleEventsTo(viewModel)
		.handleNotifications(state.notification)
		.handleNavigation(state.navigation) { dest in
			switch dest {
			case is EditProfileNavigationDestination.Back:
				navigationStack.popLast()
			case let changeEmail as EditProfileNavigationDestination.ChangeEmail:
				navigationStack.push(changeEmail)
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
