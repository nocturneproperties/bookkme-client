import SwiftUI
import shared

struct ServiceGroupListScreen: View {
	
	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: ServiceGroupListViewModel
	
	init(businessId: KotlinUuid) {
		_viewModel = StateViewModel(wrappedValue: IosServicesPresentationDiKt.serviceGroupListVM(businessId: businessId))
	}
	
	var body: some View {
		let uiState = IOSServiceGroupListState.cast(viewModel.uiState)
		VStack {
			Text("ServiceGroupList Screen")
		}
		.withNavigationBar(uiState.appBar)
		.sendLifecycleEventsTo(viewModel)
		.handleNotifications(uiState.notifications)
		.handleNavigation(uiState.navigation) { destination in
			switch destination {
			case is ServiceGroupListDestination.Back:
				navigationStack.popLast()
			default:
				break
			}
		}
	}
}
