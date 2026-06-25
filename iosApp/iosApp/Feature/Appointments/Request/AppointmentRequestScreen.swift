//
//  AppointmentRequestScreen.swift
//  iosApp
//
//  Created by BookkMe on 25.06.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct AppointmentRequestScreen: View {

    @EnvironmentObject var navigationStack: NavigationStackHolder
    @StateViewModel var viewModel: AppointmentRequestViewModel

    init(businessId: KotlinUuid) {
        self._viewModel = StateViewModel(
            wrappedValue: IosAppointmentsPresentationDiKt.appointmentRequestVM(businessId: businessId)
        )
    }

    var body: some View {
        let state = IOSAppointmentRequestState.cast(viewModel.uiState)
        VStack {
        }
        .withNavigationBar(state.appBar)
        .sendLifecycleEventsTo(viewModel)
        .handleNotifications(state.notifications)
        .handleNavigation(state.navigation) { dest in
            switch dest {
            case is AppointmentRequestDestinations.Back:
                navigationStack.popLast()
            default:
                break
            }
        }
    }
}
