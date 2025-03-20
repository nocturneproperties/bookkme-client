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
	
	@StateObject
	var viewModel = IOSSettingsDiKt.deleteAccVM()
	
	@EnvironmentObject
	var navigationStack: NavigationStackHolder
	
	var body: some View {
		let uiState = viewModel.uiState
		VStack {
			Text(uiState.confirmationMessage.localized())
				.font(.callout)
			
			StateSwitch(state: uiState.confirmationSwitch) { newState in
				viewModel.onSwitchStateChanged(isChecked: newState)
			}
			.padding(.horizontal, 16)
			.padding(.vertical, 8)
			.background(AppColors.elevated)
			.clipShape(RoundedRectangle(cornerRadius: 10))
			Spacer()
			StateButton(state: uiState.deleteButton) {
				viewModel.onDeleteClick()
			}.padding(.bottom, 16)
		}
		.padding()
		.navigationBarTitle(uiState.appBar.title.localized())
		.navigationBarTitleDisplayMode(.large)
		.sendLifecycleEventsTo(viewModel: viewModel)
		.handleNotifications(state: uiState.notifications)
	}
}
