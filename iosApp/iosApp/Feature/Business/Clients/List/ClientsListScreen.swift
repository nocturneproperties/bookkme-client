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
	
	init(businessId: KotlinUuid) {
		_viewModel = StateViewModel(wrappedValue: IOSBusinessDiKt.clientsListVM(id: businessId))
	}
	
	var body: some View {
		let uiState = IOSClientsListState.cast(viewModel.uiState)
		ScrollView {
			ContactListContent(uiState: uiState)
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
			
		}
	}
}

struct ContactListContent: View {
	
	var uiState: IOSClientsListState
	
	var body: some View {
		Group {
			if (!uiState.clientsList.items.isEmpty) {
				LazyVStack {
					ForEach(uiState.clientsList.items(ClientSection.self)) { section in
						ContactSection(section: section)
							.transition(.opacity)
							.animation(.easeInOut, value: uiState.clientsList.items.count)
					}
				}
			} else if let emptyState = uiState.clientsList.emptyState, !uiState.clientsList.isInitialLoading {
				EmptyView(state: emptyState)
			} else {
				ProgressView()
			}
		}
		.refreshable { await uiState.refreshState.impl().awaitRefresh() }
		.searchable(
			text: uiState.searchField.binding(),
			placement: .navigationBarDrawer(displayMode: .always),
			prompt: uiState.searchField.placeholder.localized()
		)
	}
}
	
struct ContactSection: View {
	
	var section: ClientSection
	
	var body: some View {
		Section {
			ForEach(section.items, id: \.id) { client in
				VStack {
					Button {
						
					} label: {
						Text(client.fullName)
							.font(.body)
							.padding(.horizontal)
							.frame(maxWidth: .infinity, minHeight: 40, alignment: .leading)
							.contentShape(Rectangle())
						Spacer()
					}
					.buttonStyle(.plain)
					
					Divider()
						.padding(.leading)
						.background(AppColors.divider)
				}
			}
		} header: {
			VStack {
				Text(section.header)
					.fontWeight(.medium)
					.foregroundStyle(AppColors.header)
					.padding(.horizontal)
					.frame(maxWidth: .infinity, minHeight: 44, alignment: .leading)
				
				Divider()
					.padding(.leading)
					.background(AppColors.divider)
			}.background(AppColors.background)
		}
	}
}
