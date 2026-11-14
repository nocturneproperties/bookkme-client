import shared
import SwiftUI

struct EditServiceScreen: View {

	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: EditServiceViewModel

	private enum FocusField {
		case name
		case duration
		case price
	}
	@FocusState private var focusedField: FocusField?

	init(serviceId: KotlinUuid) {
		_viewModel = StateViewModel(wrappedValue: IosServicesPresentationDiKt.editServiceVM(serviceId: serviceId))
	}

	var body: some View {
		let uiState = IOSEditServiceState.cast(viewModel.uiState)
		List {
			Section {
				StateTextField(uiState.name)
					.focused($focusedField, equals: .name)
					.submitLabel(.next)
					.onSubmit { focusedField = .duration }
					.textFieldStyle(.inListTrailing)
				PickerField(uiState.group)
					.textFieldStyle(.inListTrailing)
			}
			Section {
				StateTextField(uiState.duration)
					.focused($focusedField, equals: .duration)
					.submitLabel(.next)
					.onSubmit { focusedField = .price }
					.textFieldStyle(.inListTrailing)
				StateTextField(uiState.price)
					.focused($focusedField, equals: .price)
					.submitLabel(.done)
					.textFieldStyle(.inListTrailing)
			}
			Section {
				StateSwitch(state: uiState.available)
			}
			if uiState.deleteButton.isVisible {
				Section {
					TextButton(uiState.deleteButton)
						.buttonStyle(.negativeAction)
				}
			}
		}
		.toolbar {
			if uiState.submit.isVisible {
				TextButton(uiState.submit)
			}
		}
		.listSectionSpacing(.compact)
		.scrollDismissesKeyboard(.immediately)
		.withNavigationBar(uiState.appBar)
		.handleNotifications(uiState.notifications)
		.sendLifecycleEventsTo(viewModel)
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
