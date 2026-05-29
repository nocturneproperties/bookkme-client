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
		let listState = IOSListState<ServiceGroupListStateServiceGroupUI>.cast(uiState.groups)
		ListGroup(listState: listState) { group in
			Text(group.name)
		}
		.refreshable { await uiState.refreshState.impl().awaitRefresh() }
		.searchable(
			text: uiState.search.binding(),
			placement: .navigationBarDrawer(displayMode: .always),
			prompt: uiState.search.placeholder.localized()
		)
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
