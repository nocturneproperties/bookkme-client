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
	private let onCreated: ((KotlinUuid) -> Void)?
	private let onBack: (() -> Void)?
	
	init(
		businessId: KotlinUuid,
		onCreated: ((KotlinUuid) -> Void)? = nil,
		onBack: (() -> Void)? = nil
	) {
		_viewModel = StateViewModel(wrappedValue: IosClientsPresentationDiKt.createClientVM(businessId: businessId))
		self.onCreated = onCreated
		self.onBack = onBack
	}
	
	var body: some View {
		let uiState = viewModel.uiState
		List {
			Section("") {
				StateTextField(uiState.name)
					.textFieldStyle(.inListTrailing)
				StateTextField(uiState.lastName)
					.textFieldStyle(.inListTrailing)
			}
			Section {
				StateTextField(uiState.phone)
					.textFieldStyle(.inListTrailing)
				StateTextField(uiState.email)
					.textFieldStyle(.inListTrailing)
			}
		}
		.toolbar {
			TextButton(uiState.submit)
		}
		.listSectionSpacing(.compact)
		.scrollDismissesKeyboard(.immediately)
		.withNavigationBar(uiState.appBar)
		.handleNotifications(uiState.notifications)
		.sendLifecycleEventsTo(viewModel)
		.handleNavigation(uiState.navigation) { dest in
			switch dest {
			case let dest as CreateClientDestination.Details:
				if let onCreated {
					onCreated(dest.id)
				} else {
					navigationStack.popLast()
					navigationStack.push(ClientsDestinations.ClientDetails(id: dest.id))
				}
			case is CreateClientDestination.Back:
				if let onBack {
					onBack()
				} else {
					navigationStack.popLast()
				}
			default :
				break
			}
		}
	}
}

