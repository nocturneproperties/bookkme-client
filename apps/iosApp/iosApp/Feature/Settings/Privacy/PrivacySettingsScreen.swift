import shared
import SwiftUI

struct PrivacySettingsScreen: View {
	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: PrivacySettingsViewModel = IOSSettingsDiKt.privacySettingsVM()

	var body: some View {
		let state = IOSPrivacySettingsState.cast(viewModel.uiState)
		List {
			Section {
				StateSwitch(state: state.crashReporting)
			} header: {
				Text(state.diagnosticsTitle.localized())
			} footer: {
				Text(state.crashReportingDescription.localized())
			}
			.listRowBackground(AppColors.elevated)
			LegalDocumentsView(state: state.legal)
		}
		.withNavigationBar(state.appBar)
		.sendLifecycleEventsTo(viewModel)
		.handleNotifications(state.notifications)
		.handleNavigation(state.navigation) { destination in
			switch destination {
			case is PrivacySettingsDestinations.Back:
				navigationStack.popLast()
			default:
				break
			}
		}
	}
}
