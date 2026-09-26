import SwiftUI
import shared

struct AppointmentSettingsScreen: View {

    @EnvironmentObject var navigationStack: NavigationStackHolder
    @StateViewModel var viewModel: AppointmentSettingsViewModel

    init(businessId: KotlinUuid) {
        _viewModel = StateViewModel(
            wrappedValue: IosAppointmentsPresentationDiKt.appointmentSettingsVM(businessId: businessId)
        )
    }

    var body: some View {
        let state = IOSAppointmentSettingsState.cast(viewModel.uiState)
		List {
			Section {
				RequestsSettings(state: state)
			} header : {
				Text(AppointmentsRes.strings().appointments_settings_requests.desc().localized())
			} footer : {
				if let footer = state.minimalBreak.supportingTextRes {
					Text(footer.localized())
				}
			}
			Section(AppointmentsRes.strings().appointments_settings_note_header.desc().localized()) {
				StateTextField(state.note, textEditor: true)
					.lineLimit(3...5)
					.textFieldStyle(.inList)
			}
        }
		.toolbar {
			TextButton(state.save)
		}
        .scrollDismissesKeyboard(.immediately)
        .withNavigationBar(state.appBar)
        .sendLifecycleEventsTo(viewModel)
        .handleNotifications(state.notifications)
        .handleNavigation(state.navigation) { destination in
            switch destination {
            case is AppointmentSettingsDestination.Back:
                navigationStack.popLast()
            default:
                break
            }
        }
    }
}

private struct RequestsSettings: View {
    let state: IOSAppointmentSettingsState

    var body: some View {
		StateSwitch(state: state.automaticApproval) { newValue in
			state.automaticApproval.onCheckedChange?(KotlinBoolean(bool: newValue))
		}
		
		StateTextField(state.minimalBreak)
			.textFieldStyle(.inListTrailing)
    }
}
