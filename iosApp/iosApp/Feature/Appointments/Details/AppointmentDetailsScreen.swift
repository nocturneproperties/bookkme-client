//
//  AppointmentDetailsScreen.swift
//  iosApp
//
//  Created by BookkMe on 16.06.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct AppointmentDetailsScreen: View {

	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: AppointmentDetailsViewModel

	init(appointmentId: KotlinUuid) {
		_viewModel = StateViewModel(
			wrappedValue: IosAppointmentsPresentationDiKt.appointmentDetailsVM(appointmentId: appointmentId)
		)
	}

	var body: some View {
		let uiState = viewModel.uiState
		let listState = IOSListState<InfoLine>.cast(uiState.infoSections)
		ListGroup(listState: listState) { section in
			InfoSection(section: section)
		}
		.padding(.top)
		.withNavigationBar(uiState.appBar)
		.handleNotifications(uiState.notifications)
		.sendLifecycleEventsTo(viewModel)
		.handleNavigation(uiState.navigation) { dest in
			switch dest {
			case is AppointmentDetailsDestination.Back:
				navigationStack.popLast()
			default :
				break
			}
		}
	}
}
