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
        VStack {
            // TODO: Implement screen content
        }
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
