//
//  CreateClientScreen.swift
//  iosApp
//
//  Created by BookkMe on 05.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct CreateClientScreen: View {
	
	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: CreateClientViewModel
	
	init(businessId: KotlinUuid) {
		_viewModel = StateViewModel(wrappedValue: IosClientsPresentationDiKt.createClientVM(businessId: businessId))
	}
	
	var body: some View {
		let uiState = viewModel.uiState
		VStack {
			StateTextField(uiState.name)
			StateTextField(uiState.lastName)
			StateTextField(uiState.phone)
			StateButton(uiState.submit)
				.padding(.top, 20)
			Spacer()
		}
		.padding()
		.withNavigationBar(uiState.appBar)
		.handleNotifications(uiState.notifications)
		.sendLifecycleEventsTo(viewModel)
		.handleNavigation(uiState.navigation) { dest in
			switch dest {
			case let dest as CreateClientDestination.Details:
				navigationStack.popLast()
				navigationStack.push(ClientsDestinations.ClientDetails(id: dest.id))
			case is CreateClientDestination.Back:
				navigationStack.popLast()
			default :
				break
			}
		}
	}
}

