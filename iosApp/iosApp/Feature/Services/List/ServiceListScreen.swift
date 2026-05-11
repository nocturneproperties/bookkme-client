//
//  ServiceListScreen.swift
//  iosApp
//
//  Created by BookkMe on 11.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct ServiceListScreen: View {
	
	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: ServiceListViewModel
	
	init(businessId: KotlinUuid) {
		_viewModel = StateViewModel(wrappedValue: IosServicesPresentationDiKt.serviceListVM(businessId: businessId))
	}
	
	var body: some View {
		let uiState = IOSServiceListState.cast(viewModel.uiState)
		let listState = IOSListState<ServiceListStateServiceGroupUI>.cast(uiState.services)
		ListGroup(listState: listState) { section in
			ServiceGroupSection(section: section)
				.transition(.opacity)
				.animation(.easeInOut, value: uiState.services.items.count)
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
			case let destination as ServiceListDestination.ServiceDetails:
				break
			case let destination as ServiceListDestination.AddService:
				break
			case is ServiceListDestination.Back:
				navigationStack.popLast()
			default:
				break
			}
		}
	}
}

struct ServiceGroupSection: View {
	
	var section: ServiceListStateServiceGroupUI
	
	var body: some View {
		Section {
			ForEach(section.items, id: \.id) { service in
				VStack {
					Button {
						section.onItemClick(service)
					} label: {
						VStack {
							Spacer()
							Text(service.title)
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
				Text(section.name)
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
