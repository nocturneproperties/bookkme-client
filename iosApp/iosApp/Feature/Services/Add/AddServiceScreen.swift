//
//  AddServiceScreen.swift
//  iosApp
//
//  Created by BookkMe on 29.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import shared
import SwiftUI

struct AddServiceScreen: View {
	
	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: AddServiceViewModel
	
	init(businessId: KotlinUuid) {
		_viewModel = StateViewModel(wrappedValue: IosServicesPresentationDiKt.addServiceVM(businessId: businessId))
	}
	
	var body: some View {
		let uiState = IOSAddServiceState.cast(viewModel.uiState)
		VStack {
			PickerField(uiState.group)
			StateTextField(uiState.name)
			StateTextField(uiState.duration)
			StateTextField(uiState.price)
			StateCheckBox(uiState.enabled_)
				.padding(.vertical)
			StateButton(uiState.create)
			Spacer()
		}
		.padding()
		.withNavigationBar(uiState.appBar)
		.sendLifecycleEventsTo(viewModel)
		.handleNotifications(uiState.notifications)
		.handleNavigation(uiState.navigation) { destination in
			switch destination {
			case is AddServiceDestination.Back:
				navigationStack.popLast()
			default:
				break
			}
		}
	}
}

#Preview {
	AddServiceScreen(businessId: KotlinUuid.companion.random())
}
