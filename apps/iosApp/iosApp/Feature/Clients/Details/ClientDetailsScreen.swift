//
//  ClientDetailsScreen.swift
//  iosApp
//
//  Created by BookkMe on 07.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct ClientDetailsScreen: View {
	
	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: ClientDetailsViewModel
	
	init(id: KotlinUuid) {
		_viewModel = StateViewModel(wrappedValue: IosClientsPresentationDiKt.createClientDetailsVM(id: id))
	}
	
	var body: some View {
		let uiState = viewModel.uiState
		let listState = IOSListState<InfoLine>.cast(uiState.infoSections)
		ListGroup(listState: listState) { section in
			InfoSection(section: section)
		}
		.withNavigationBar(uiState.appBar)
		.handleNotifications(uiState.notifications)
		.sendLifecycleEventsTo(viewModel)
		.handleNavigation(uiState.navigation) { dest in
			switch dest {
			case is ClientDetailsDestination.Back:
				navigationStack.popLast()
			case let dest as ClientDetailsDestination.Edit:
				navigationStack.push(ClientsDestinations.EditClient(id: dest.id))
			default :
				break
			}
		}
	}
}
