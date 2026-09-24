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
		let listState = IOSListState<ClientSection>.cast(uiState.clientsList)
		ListGroup(listState: listState) { section in
			ContactSection(section: section)
				.listRowSeparator(.hidden)
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
	
struct ContactSection: View {
	
	var section: ClientSection
	
	var body: some View {
		Section {
			ForEach(section.items, id: \.id) { client in
				VStack {
					Button {
						section.onItemClick(client)
					} label: {
						VStack {
							Spacer()
							Text(client.fullName)
								.font(.body)
								.padding(.horizontal)
								.frame(maxWidth: .infinity, minHeight: 44, alignment: .init(horizontal: .leading, vertical: .center))
								.contentShape(Rectangle())
							Spacer()
						}
					}
					.buttonStyle(.plain)
					
					Divider()
						.padding(.leading)
						.background(AppColors.divider)
				}.listRowSeparator(.hidden)
			}
		} header: {
			VStack {
				Text(section.header)
					.fontWeight(.medium)
					.foregroundStyle(AppColors.header)
					.padding(.horizontal)
					.frame(maxWidth: .infinity, alignment: .init(horizontal: .leading, vertical: .center))
				
				Divider()
					.padding(.leading)
					.background(AppColors.divider)
			}.background(AppColors.background)
		}.listRowInsets(EdgeInsets())
	}
}
