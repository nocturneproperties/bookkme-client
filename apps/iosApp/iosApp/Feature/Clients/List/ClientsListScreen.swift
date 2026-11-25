//
//  ClientsListScreen.swift
//  iosApp
//
//  Created by BookkMe on 03.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct ClientsListScreen: View {
	
	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: ClientsListViewModel
	
	init() {
		_viewModel = StateViewModel(wrappedValue: IosClientsPresentationDiKt.clientsListVM())
	}
	
	var body: some View {
		let uiState = IOSClientsListState.cast(viewModel.uiState)
		let listState = IOSListState<ClientListItem>.cast(uiState.clientsList)
		ListGroup(listState: listState) { item in
			ClientListRow(item: item)
				.listRowSeparator(.hidden)
				.listRowInsets(EdgeInsets())
				.transition(.opacity)
				.animation(.easeInOut, value: uiState.clientsList.items.count)
		}
		.refreshable { await uiState.refreshState.impl().awaitRefresh() }
		.searchable(
			text: uiState.searchField.binding(),
			placement: .navigationBarDrawer(displayMode: .always),
			prompt: uiState.searchField.placeholder.localized()
		)
		.withNavigationBar(uiState.appBar)
		.fullScreenCover(isPresented: Binding(
			get: { uiState.isContactsPickerVisible },
			set: { uiState.isContactsPickerVisible = $0 }
		)) {
			let contactsPicker = uiState.contactsPicker.impl()
			OptionsMultiPickerScreen(
				args: .from(
					id: contactsPicker.id,
					title: contactsPicker.pickerTitle,
					options: contactsPicker.options,
					choice: .multiple
				),
				options: contactsPicker.options,
				onItemsPicked: contactsPicker.onItemsPicked
			)
		}
		.sendLifecycleEventsTo(viewModel)
		.handleNotifications(uiState.notifications)
		.handleNavigation(uiState.navigation) { destination in
			switch destination {
			case let destination as ClientsListDestination.ClientDetails:
				navigationStack.push(ClientsDestinations.ClientDetails(id: destination.clientId))
			case let destination as ClientsListDestination.AddClient:
				navigationStack.push(ClientsDestinations.CreateClient(businessId: destination.businessId))
			case is ClientsListDestination.Back:
				navigationStack.popLast()
			default:
				break
			}
		}
	}
}
	
struct ClientListRow: View {
	
	var item: ClientListItem
	
	var body: some View {
		VStack(spacing: 0) {
			Button {
				item.onClick()
			} label: {
				Text(item.fullName)
					.font(.body)
					.padding(.horizontal)
					.frame(maxWidth: .infinity, minHeight: 44, alignment: .leading)
					.contentShape(Rectangle())
			}
			.buttonStyle(.plain)
			
			Divider()
				.padding(.leading)
				.background(AppColors.divider)
		}
	}
}
