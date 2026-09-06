import SwiftUI
import shared

struct ServiceGroupListScreen: View {
	
	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: ServiceGroupListViewModel

	init() {
		_viewModel = StateViewModel(wrappedValue: IosServicesPresentationDiKt.serviceGroupListVM())
	}
	
	var body: some View {
		let uiState = IOSServiceGroupListState.cast(viewModel.uiState)
		let listState = IOSListState<ServiceGroupListStateServiceGroupUI>.cast(uiState.groups)
		ListGroup(listState: listState, listStyle: .automatic) { group in
			Text(group.name)
				.transition(.opacity)
				.animation(.easeInOut, value: listState.items.count)
				.swipeActions(edge: .trailing, allowsFullSwipe: false) {
					Button(DesignSystem.strings().action_delete.desc().localized()) {
						group.onDeleteClick()
					}
					.tint(.red)
				}
				.contextMenu {
					Button(DesignSystem.strings().action_delete.desc().localized(), role: .destructive) {
						group.onDeleteClick()
					}
				}
				.id(group.id)
		}
		.sheet(isPresented: uiState.isAddGroupDialogVisibleBinding) {
			AddServiceGroupScreen {
				uiState.isAddGroupDialogVisible.toggle()
			}
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
