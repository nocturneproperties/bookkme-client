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
		ListGroup(listState: listState, listStyle: .automatic) { section in
			ServiceGroupSection(section: section)
				.transition(.opacity)
				.animation(.easeInOut, value: uiState.services.items.count)
				.id(section.id)
		} header: {
			Section {
				SectionView(action: uiState.groupsSection)
			}
			.listRowSeparator(.hidden)
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
			case let destination as ServiceListDestination.ServiceGroups:
				navigationStack.push(ServicesDestination.ServiceGroupList(businessId: destination.businessId))
				break
			case let destination as ServiceListDestination.AddService:
				navigationStack.push(ServicesDestination.AddService(businessId: destination.businessId))
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
		Section(section.name) {
			ForEach(section.items, id: \.id) { service in
				Button {
					section.onItemClick(service)
				} label: {
					Text(service.title)
						.font(.body)
						.frame(maxWidth: .infinity, alignment: .init(horizontal: .leading, vertical: .center))
						.contentShape(Rectangle())
				}
				.buttonStyle(.plain)
				.swipeActions(edge: .trailing, allowsFullSwipe: false) {
					Button(DesignSystem.strings().action_delete.desc().localized()) {
						section.onItemDeleteClick(service)
					}
					.tint(.red)
				}
				.contextMenu {
					Button(DesignSystem.strings().action_delete.desc().localized(), role: .destructive) {
						section.onItemDeleteClick(service)
					}
				}
				.id(service.id)
			}
		}.id(section.id)
	}
}
