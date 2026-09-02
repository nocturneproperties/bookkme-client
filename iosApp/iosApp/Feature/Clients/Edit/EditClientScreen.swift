//
//  EditClientScreen.swift
//  iosApp
//
//  Created by BookkMe on 02.09.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct EditClientScreen: View {

	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: EditClientViewModel

	init(id: KotlinUuid) {
		_viewModel = StateViewModel(wrappedValue: IosClientsPresentationDiKt.editClientVM(id: id))
	}

	var body: some View {
		let uiState = viewModel.uiState
		List {
			if uiState.isAttachedInfoVisible {
				Section {
					Text(uiState.attachedInfoText.localized())
						.font(.footnote)
						.foregroundStyle(AppColors.secondary)
				}
			}
			Section {
				StateTextField(uiState.name)
					.textFieldStyle(.inListTrailing)
				StateTextField(uiState.lastName)
					.textFieldStyle(.inListTrailing)
			}
			Section {
				StateTextField(uiState.phone)
					.textFieldStyle(.inListTrailing)
				StateTextField(uiState.email)
					.textFieldStyle(.inListTrailing)
			}
			Section {
				StateTextField(uiState.description_, textEditor: true)
					.lineLimit(3...5)
					.textFieldStyle(.inList)
			}
			Section {
				TextButton(uiState.deleteButton)
					.buttonStyle(.negativeAction)
			}
		}
		.toolbar {
			TextButton(uiState.submit)
		}
		.listSectionSpacing(.compact)
		.scrollDismissesKeyboard(.immediately)
		.withNavigationBar(uiState.appBar)
		.handleNotifications(uiState.notifications)
		.sendLifecycleEventsTo(viewModel)
		.handleNavigation(uiState.navigation) { dest in
			switch dest {
			case is EditClientDestination.Back:
				navigationStack.popLast()
			case is EditClientDestination.Deleted:
				navigationStack.popLast()
				navigationStack.popLast()
			default:
				break
			}
		}
	}
}
