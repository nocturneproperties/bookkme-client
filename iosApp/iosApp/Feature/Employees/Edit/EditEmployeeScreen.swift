import SwiftUI
import shared

struct EditEmployeeScreen: View {
	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: EditEmployeeViewModel

	init(id: KotlinUuid) {
		_viewModel = StateViewModel(wrappedValue: IosEmployeesPresentationDiKt.editEmployeeVM(id: id))
	}

	var body: some View {
		let state = IOSEditEmployeeState.cast(viewModel.uiState)
		List {
			Section {
				ForEach(state.contacts.items(InfoLine.self)) { line in
					InfoSection(section: line)
				}
			}

			Section {
				OptionsMultiPickerField(state.services) { item, onRemove in
					EmployeeServiceItem(
						service: item as! EmployeeServicePresentation,
						onRemove: state.services.isEditable ? onRemove : nil
					)
				}
				.listRowInsets(EdgeInsets())
			} header: {
				Text(state.services.pickerTitle.localized())
			}

			ScheduleSections(state: state.schedule)

			if state.isPermissionsVisible {
				ForEach(state.permissions.items(ResourcePermissionState.self), id: \.id) { permission in
					ResourcePermissionSection(state: permission)
				}
			} else if let hint = state.permissionsHint {
				Section {
				} header: {
					Text(EmployeesRes.strings().employees_edit_permissions.desc().localized())
				} footer: {
					Text(hint.localized())
				}
			}
		}
		.listSectionSpacing(.compact)
		.toolbar {
			if state.save.isVisible {
				TextButton(state.save)
			}
		}
		.withNavigationBar(state.appBar)
		.sendLifecycleEventsTo(viewModel)
		.handleNotifications(state.notifications)
		.handleNavigation(state.navigation) { destination in
			switch destination {
			case is EditEmployeeDestinations.Back:
				navigationStack.popLast()
			default:
				break
			}
		}
	}
}

private struct ResourcePermissionSection: View {
	let state: any ResourcePermissionState

	var body: some View {
		Section {
			StateSwitch(state: state.canView)
			StateSwitch(state: state.canUpdate)
			StateSwitch(state: state.canDelete)
		} header: {
			Text(state.title.localized())
		}
	}
}

private struct EmployeeServiceItem: View {
	let service: EmployeeServicePresentation
	let onRemove: (() -> Void)?

	var body: some View {
		HStack(spacing: 0) {
			VStack(alignment: .leading, spacing: 2) {
				Text(service.displayName.localized())
					.font(.headline)
					.foregroundStyle(AppColors.primary)
				Text(service.duration.localized())
					.font(.caption)
					.foregroundStyle(AppColors.secondary)
			}
			.padding(.leading, 16)
			Spacer(minLength: 8)
			Text(service.price)
				.font(.headline)
				.foregroundStyle(AppColors.primary)
			if let onRemove {
				Button(action: onRemove) {
					ZStack {
						Circle()
							.fill(AppColors.primary.opacity(0.1))
							.frame(width: 28, height: 28)
						Image(systemName: "minus")
							.font(.body.bold())
							.foregroundStyle(AppColors.secondary)
					}
				}
				.buttonStyle(.plain)
				.padding(.leading, 8)
				.padding(.trailing, 16)
			} else {
				Spacer()
					.frame(width: 16)
			}
		}
		.padding(.vertical, 16)
	}
}
