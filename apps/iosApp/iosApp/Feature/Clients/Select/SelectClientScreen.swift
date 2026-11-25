import SwiftUI
import shared

struct SelectClientScreen: View {

	@StateViewModel var viewModel: SelectClientViewModel
	let onSelected: (KotlinUuid) -> Void
	let onDismiss: () -> Void

	init(onSelected: @escaping (KotlinUuid) -> Void, onDismiss: @escaping () -> Void) {
		_viewModel = StateViewModel(wrappedValue: IosClientsPresentationDiKt.selectClientVM())
		self.onSelected = onSelected
		self.onDismiss = onDismiss
	}

	var body: some View {
		let uiState = IOSSelectClientState.cast(viewModel.uiState)
		let listState = IOSListState<ClientListItem>.cast(uiState.clients)
		NavigationStack {
			ListGroup(listState: listState) { item in
				ClientListRow(item: item)
					.listRowSeparator(.hidden)
					.listRowInsets(EdgeInsets())
			}
			.refreshable { await uiState.refreshState.impl().awaitRefresh() }
			.searchable(
				text: uiState.searchField.binding(),
				placement: .navigationBarDrawer(displayMode: .always),
				prompt: uiState.searchField.placeholder.localized()
			)
			.withNavigationBar(uiState.appBar)
		}
		.sendLifecycleEventsTo(viewModel)
		.handleNotifications(uiState.notifications)
		.handleNavigation(uiState.navigation) { destination in
			switch destination {
			case let destination as SelectClientDestination.Selected:
				onSelected(destination.clientId)
			case is SelectClientDestination.Back:
				onDismiss()
			default:
				break
			}
		}
	}
}
