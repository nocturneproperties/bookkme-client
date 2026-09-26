import SwiftUI
import shared

struct EmployeeListScreen: View {
	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: EmployeeListViewModel

	init() {
		_viewModel = StateViewModel(wrappedValue: IosEmployeesPresentationDiKt.employeeListVM())
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
			case let destination as EmployeeListDestinations.EditEmployee:
				navigationStack.push(EmployeesDestinations.EditEmployee(id: destination.id))
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
				Button {
					section.onItemClick(employee)
				} label: {
					VStack {
						Text(employee.fullName)
							.font(.body)
							.padding(.horizontal)
							.frame(maxWidth: .infinity, minHeight: 44, alignment: .init(horizontal: .leading, vertical: .center))
						Divider()
							.padding(.leading)
							.background(AppColors.divider)
					}
					.contentShape(Rectangle())
				}
				.buttonStyle(.plain)
				.listRowSeparator(.hidden)
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
