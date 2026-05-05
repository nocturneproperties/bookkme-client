//
//  ContactUsView.swift
//  iosApp
//
//  Created by BookkMe on 21.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared
import SwiftUI

struct ContactUsScreen: View {
	
	@StateViewModel
	var viewModel = IOSSettingsDiKt.contactUsVM()
	
	@EnvironmentObject
	var navigationStack: NavigationStackHolder
	
	var body: some View {
		let uiState = viewModel.uiState
		VStack {
			StateTextField(uiState.contactField, textEditor: true) { text in
				viewModel.onContactTextChanged(text: text)
			}
			.lineLimit(5...10)
			StateSwitch(state: uiState.includeLogsSwitch) { checked in
				viewModel.onIncludeLogsStateChanged(include: checked)
			}
			.padding(.horizontal)
			.padding(.vertical, 8)
			.background(AppColors.elevated)
			.clipShape(RoundedRectangle(cornerRadius: 10))
			Text(uiState.logsExplanationText.localized())
				.font(.footnote)
				.foregroundStyle(AppColors.secondary)
			Spacer()
			StateButton(uiState.submitButton) {
				viewModel.onSubmitClick()
			}
			.padding(.bottom, 24)
		}
		.padding()
		.background(AppColors.background)
		.navigationBarTitle(uiState.appBar.title.localized())
		.navigationBarTitleDisplayMode(.large)
		.sendLifecycleEventsTo(viewModel)
		.handleNotifications(uiState.notifications)
		.handleNavigation(uiState.navigation) { destination in
			switch destination {
			case is ContactUsNavigationDestination.Back:
				navigationStack.path.removeLast()
				break
			default:
				break
			}
		}
	}
}
