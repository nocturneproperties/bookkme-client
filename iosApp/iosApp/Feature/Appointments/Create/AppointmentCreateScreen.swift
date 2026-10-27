import SwiftUI
import shared

struct AppointmentCreateScreen: View {

    @EnvironmentObject var navigationStack: NavigationStackHolder
    @StateViewModel var viewModel: AppointmentCreateViewModel

    init(businessId: KotlinUuid) {
        _viewModel = StateViewModel(
            wrappedValue: IosAppointmentsPresentationDiKt.appointmentCreateVM(businessId: businessId)
        )
    }

    var body: some View {
        let state = IOSAppointmentCreateState.cast(viewModel.uiState)
        List {
			Section {
				PickerField(state.clientPicker)
					.textFieldStyle(.inListTrailing)
			} header: {
				Text("")
			}
			Section {
				OptionsMultiPickerField(state.servicePicker) { item, onRemove in
					AppointmentServiceItem(
						service: item as! ServicePickerPresentation,
						onRemove: onRemove
					)
				}
				.listRowInsets(EdgeInsets())
			} header: {
				Text(state.servicePicker.pickerTitle.localized())
			} footer: {
				if !state.subtotalPrice.isEmpty {
					AppointmentSubtotalRow(label: state.subtotalLabel, price: state.subtotalPrice)
						.listRowInsets(EdgeInsets())
						.listRowBackground(Color.clear)
				}
			}
			Section {
				DatePickerField(state: state.datePicker)
					.textFieldStyle(.inList)
				TimePickerField(state: state.timePicker)
					.textFieldStyle(.inList)
			} footer: {
				if let error = state.datePicker.textField.supportingTextRes {
					Text(error.localized())
						.foregroundStyle(AppColors.error)
				}
				if let error = state.timePicker.textField.supportingTextRes {
					Text(error.localized())
						.foregroundStyle(AppColors.error)
				}
			}
			Section {
				StateTextField(state.note, textEditor: true)
					.lineLimit(3...5)
					.textFieldStyle(.inList)
			}
        }
		.toolbar {
			TextButton(state.create)
		}
		.listSectionSpacing(.compact)
        .scrollDismissesKeyboard(.immediately)
        .withNavigationBar(state.appBar)
        .sendLifecycleEventsTo(viewModel)
        .handleNotifications(state.notifications)
        .handleNavigation(state.navigation) { destination in
            switch destination {
            case is AppointmentCreateDestination.Back:
                navigationStack.popLast()
            default:
                break
            }
        }
    }
}
