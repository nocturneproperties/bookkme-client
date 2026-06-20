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
        ScrollView {
            VStack(spacing: 16) {
                StateButton(state.save)
                    .padding(.top, 16)
            }
            .padding(16)
        }
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
