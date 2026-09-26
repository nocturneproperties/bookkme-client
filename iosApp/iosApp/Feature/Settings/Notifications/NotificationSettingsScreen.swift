//
//  NotificationSettingsScreen.swift
//  iosApp
//
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct NotificationSettingsScreen: View {

	@EnvironmentObject var navigationStack: NavigationStackHolder

	@StateViewModel
	var viewModel: NotificationSettingsViewModel = IOSSettingsDiKt.notificationSettingsVM()

	var body: some View {
		let state = viewModel.uiState
		List {
			Section(SettingsRes.strings().settings_notifications_channels_header.desc().localized()) {
				if state.emailEnabled.isVisible {
					StateSwitch(state: state.emailEnabled)
				}
				if state.pushNotificationsEnabled.isVisible {
					StateSwitch(state: state.pushNotificationsEnabled)
				}
				if state.telegramEnabled.isVisible {
					StateSwitch(state: state.telegramEnabled)
				}
			}
			Section {
				StateSwitch(state: state.appointmentEnabled)
			} header: {
				Text(SettingsRes.strings().settings_notifications_types_header.desc().localized())
			} footer: {
				Text(SettingsRes.strings().settings_notifications_types_footer.desc().localized())
			}
		}
		.scrollDismissesKeyboard(.immediately)
		.withNavigationBar(state.appBar)
		.sendLifecycleEventsTo(viewModel)
		.handleNotifications(state.notifications)
		.handleNavigation(state.navigation) { dest in
			switch dest {
			case is NotificationSettingsDestinations.Back:
				navigationStack.popLast()
			default:
				break
			}
		}
	}
}

#Preview {
	NotificationSettingsScreen()
}
