//
//  NotificationSettingsScreen.swift
//  iosApp
//
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct NotificationSettingsScreen: View {

	@StateViewModel
	var viewModel: NotificationSettingsViewModel = IOSSettingsDiKt.notificationSettingsVM()

	var body: some View {
		let state = viewModel.uiState
		VStack {
		}
		.withNavigationBar(state.appBar)
		.sendLifecycleEventsTo(viewModel)
		.handleNotifications(state.notifications)
	}
}

#Preview {
	NotificationSettingsScreen()
}
