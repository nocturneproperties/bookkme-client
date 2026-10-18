import shared
import SwiftUI

struct EditServiceScreen: View {

	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: EditServiceViewModel

	init(serviceId: KotlinUuid) {
		_viewModel = StateViewModel(wrappedValue: IosServicesPresentationDiKt.editServiceVM(serviceId: serviceId))
	}

	var body: some View {
		let uiState = IOSEditServiceState.cast(viewModel.uiState)
		List {}
			.withNavigationBar(uiState.appBar)
			.sendLifecycleEventsTo(viewModel)
			.handleNotifications(uiState.notifications)
			.handleNavigation(uiState.navigation) { destination in
				switch destination {
				case is EditServiceDestination.Back:
					navigationStack.popLast()
				default:
					break
				}
			}
	}
}
