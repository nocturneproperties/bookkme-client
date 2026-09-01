import SwiftUI
import shared

struct EmployeeListScreen: View {
	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: EmployeeListViewModel

	init(businessId: KotlinUuid) {
		_viewModel = StateViewModel(wrappedValue: IosEmployeesPresentationDiKt.employeeListVM(id: businessId))
	}

	var body: some View {
		let uiState = IOSEmployeeListState.cast(viewModel.uiState)
		let listState = IOSListState<EmployeeSection>.cast(uiState.employeesList)
		ListGroup(listState: listState) { section in
			EmployeeSectionRow(section: section)
				.listRowSeparator(.hidden)
				.transition(.opacity)
				.animation(.easeInOut, value: uiState.employeesList.items.count)
		}
		.refreshable { await uiState.refreshState.impl().awaitRefresh() }
		.searchable(
			text: uiState.searchField.binding(),
			placement: .navigationBarDrawer(displayMode: .always),
			prompt: uiState.searchField.placeholder.localized()
		)
		.withNavigationBar(uiState.appBar)
		.sendLifecycleEventsTo(viewModel)
		.handleNotifications(uiState.notifications)
		.handleNavigation(uiState.navigation) { destination in
			switch destination {
			case is EmployeeListDestinations.Back:
				navigationStack.popLast()
			case let destination as EmployeeListDestinations.AddEmployee:
				navigationStack.push(EmployeesDestinations.InviteEmployee(businessId: destination.businessId))
			default:
				break
			}
		}
	}
}

struct EmployeeSectionRow: View {
	var section: EmployeeSection

	var body: some View {
		Section {
			ForEach(section.items, id: \.id) { employee in
				VStack {
					Text(employee.fullName)
						.font(.body)
						.padding(.horizontal)
						.frame(maxWidth: .infinity, minHeight: 44, alignment: .init(horizontal: .leading, vertical: .center))
					Divider()
						.padding(.leading)
						.background(AppColors.divider)
				}.listRowSeparator(.hidden)
			}
		} header: {
			VStack {
				Text(section.header)
					.fontWeight(.medium)
					.foregroundStyle(AppColors.header)
					.padding(.horizontal)
					.frame(maxWidth: .infinity, alignment: .init(horizontal: .leading, vertical: .center))
				Divider()
					.padding(.leading)
					.background(AppColors.divider)
			}.background(AppColors.background)
		}.listRowInsets(EdgeInsets())
	}
}
