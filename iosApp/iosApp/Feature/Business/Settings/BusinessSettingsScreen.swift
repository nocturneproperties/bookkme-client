//
//  BusinessSettingsScreen'.swift
//  iosApp
//
//  Created by BookkMe on 07.09.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//
import SwiftUI
import shared

struct BusinessSettingsScreen: View {
	
	@StateObject
	var viewModel: BusinessSettingsViewModel = IOSBusinessDiKt.businessSettingsVM()
	
	var body: some View {
		BusinessSettingsContent(state: viewModel.uiState)
			.withNavigationBar(state: viewModel.uiState.appBar)
			.handleNotifications(state: viewModel.uiState.notifications)
			.sendLifecycleEventsTo(viewModel: viewModel)
			.toolbar {
				TextButton(state: viewModel.uiState.save) {
					
				}
			}
	}
}

struct BusinessSettingsContent: View {
	@ObservedObject
	var state: IOSBusinessSettingsState
	
	init(state: BusinessSettingsState) {
		self.state = IOSBusinessSettingsState.cast(kotlinState: state)
	}
	
	var body: some View {
		VStack {
			PlainList {
				Section {
					StateTextField(state: state.name) { text in
						
					}
				} header: {
					Text(BusinessRes.strings().business_settings_name_title.desc().localized())
						.textCase(.uppercase)
						.padding(.leading)
				}
				.listRowInsets(EdgeInsets())
				.listRowSeparator(.hidden)
				Section{
					StateTextField(state: state.description_, textEditor: true) { text in
						
					}
					.fixedSize(horizontal: false, vertical: true)
					.lineLimit(3, reservesSpace: true)
				} header: {
					Text(BusinessRes.strings().business_settings_description_title.desc().localized())
						.textCase(.uppercase)
						.padding(.leading)
				}
				.listRowInsets(EdgeInsets())
				.listRowSeparator(.hidden)
				Section {
					StateTextField(state: state.location) { text in
						
					}
					TextButton(state: state.testLocation, textAlignment: .leading) {
						
					}
					.padding(.top, 8)
					.foregroundColor(.accentColor)
				} header: {
					Text(BusinessRes.strings().business_settings_location_title.desc().localized())
						.textCase(.uppercase)
						.padding(.leading)
				}
				.buttonStyle(.plain)
				.listRowInsets(EdgeInsets())
				.listRowSeparator(.hidden)
				.listRowBackground(Color.clear)
				Section {
					StateTextField(state: state.address) { text in
						
					}
				} header: {
					Text(BusinessRes.strings().business_settings_address_title.desc().localized())
						.textCase(.uppercase)
						.padding(.leading)
				}
				.listRowInsets(EdgeInsets())
				.listRowSeparator(.hidden)
				Section {
					StatePicker<CurrencyUI>(state: state.currency) { option in
						
					}
				} header: {
					Text(BusinessRes.strings().business_settings_currency_title.desc().localized())
						.textCase(.uppercase)
						.padding(.leading)
				}
				.listRowInsets(EdgeInsets())
				.listRowSeparator(.hidden)
				Section {
					StateTextField(state: state.instagram) { text in
						
					}
					StateTextField(state: state.telegram) { text in
						
					}
					StateTextField(state: state.viber) { text in
						
					}
				} header: {
					Text(BusinessRes.strings().business_settings_socials_title.desc().localized())
						.textCase(.uppercase)
						.padding(.leading)
				}
				.listRowInsets(EdgeInsets())
			}
		}
	}
}

#Preview {
	NavigationStack {
		BusinessSettingsScreen()
	}
}
