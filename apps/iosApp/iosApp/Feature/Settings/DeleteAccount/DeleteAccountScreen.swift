//
//  DeleteAccountScreen.swift
//  iosApp
//
//  Created by BookkMe on 19.03.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared
import SwiftUI

struct DeleteAccountScreen: View {
	
	@StateViewModel
	var viewModel = IOSSettingsDiKt.deleteAccVM()
	
	@EnvironmentObject
	var navigationStack: NavigationStackHolder
	
	var body: some View {
		let uiState = viewModel.uiState
		VStack {
			if let pendingDeletion = uiState.pendingDeletion {
				InfoNote(text: pendingDeletion.localized())
			}
			if uiState.confirmation.isVisible {
				Text(uiState.confirmationMessage.localized())
					.font(.callout)
				
				StateSwitch(state: uiState.confirmation)
				.padding(.horizontal, 16)
				.padding(.vertical, 8)
				.background(AppColors.elevated)
				.clipShape(RoundedRectangle(cornerRadius: 10))
			}
			Spacer()
			if uiState.deleteButton.isVisible {
				StateButton(uiState.deleteButton)
					.padding(.bottom, 16)
			}
		}
		.padding()
		.background(AppColors.background)
		.navigationBarTitle(uiState.appBar.title.localized())
		.navigationBarTitleDisplayMode(.large)
		.sendLifecycleEventsTo(viewModel)
		.handleNotifications(uiState.notifications)
	}
}

#Preview {
	DeleteAccountScreen()
}
