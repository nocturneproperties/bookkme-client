//
//  AddServiceGroupScreen.swift
//  iosApp
//
//  Created by BookkMe on 30.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import shared
import SwiftUI

struct AddServiceGroupScreen: View {
	
	@StateViewModel var viewModel: AddGroupViewModel
	let onDismiss: () -> Void

	init(onDismiss: @escaping () -> Void) {
		_viewModel = StateViewModel(wrappedValue: IosServicesPresentationDiKt.addGroupVM())
		self.onDismiss = onDismiss
	}
	
	var body: some View {
		let uiState = viewModel.uiState
		NavigationStack {
			VStack {
				StateTextField(uiState.name)
					.padding(.top, 42)
					.textFieldStyle(.onElevated)
				Spacer()
				StateButton(uiState.create)
			}
			.navigationTitle(uiState.title.localized())
			.presentationBackground(AppColors.elevated)
			.presentationDetents([.height(400)])
			.padding()
			.sendLifecycleEventsTo(viewModel)
			.handleNotifications(uiState.notifications)
			.handleNavigation(uiState.navigation) { dest in
				if dest is AddGroupNavigation.Dismiss {
					onDismiss()
				}
			}
		}
	}
}
