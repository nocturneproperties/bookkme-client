import SwiftUI
import shared

struct InviteEmployeeScreen: View {
	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: InviteEmployeeViewModel

	init(businessId: KotlinUuid) {
		_viewModel = StateViewModel(wrappedValue: IosEmployeesPresentationDiKt.inviteEmployeeVM(businessId: businessId))
	}

	var body: some View {
		let uiState = IOSInviteEmployeeState.cast(viewModel.uiState)
		Color.clear
			.withNavigationBar(uiState.appBar)
			.sendLifecycleEventsTo(viewModel)
			.handleNotifications(uiState.notifications)
			.handleNavigation(uiState.navigation) { destination in
				switch destination {
				case is InviteEmployeeDestinations.Back:
					navigationStack.popLast()
				default:
					break
				}
			}
	}
}
