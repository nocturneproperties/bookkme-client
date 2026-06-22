//
//  BusinessSettingsScreen.swift
//  iosApp
//
//  Created by BookkMe on 07.09.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//
import SwiftUI
import shared

struct BusinessSettingsScreen: View {
	
	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: BusinessSettingsViewModel
	
	init(id: shared.KotlinUuid) {
		self._viewModel = StateViewModel(wrappedValue: IOSBusinessDiKt.businessSettingsVM(id: id))
	}
	
	var body: some View {
		List {
			BusinessSettingsContent(viewModel: viewModel)
		}
		.toolbar {
			TextButton(viewModel.uiState.save) {
				viewModel.onSaveClick()
			}
			.frame(width: 100)
		}
		.listSectionSpacing(.compact)
		.withNavigationBar(viewModel.uiState.appBar)
		.handleNotifications(viewModel.uiState.notifications)
		.sendLifecycleEventsTo(viewModel)
		.handleNavigation(viewModel.uiState.navigation) { dest in
			switch dest {
			case is BusinessSettingsDestination.Back:
				navigationStack.popLast()
			default :
				break
			}
		}
	}
}

struct BusinessSettingsContent: View {
	
	let state: IOSBusinessSettingsState
	let viewModel: BusinessSettingsViewModel
	
	init(viewModel: BusinessSettingsViewModel) {
		self.state = IOSBusinessSettingsState.cast(viewModel.uiState)
		self.viewModel = viewModel
	}
	
	var body: some View {
		Section(BusinessRes.strings().business_settings_name_title.desc().localized()) {
			StateTextField(state.name) { text in
				viewModel.onNameChanged(name: text)
			}
			.textFieldStyle(.inList)
		}
		
		Section(BusinessRes.strings().business_settings_description_title.desc().localized()) {
			StateTextField(state.description_, textEditor: true) { text in
				viewModel.onDescriptionChanged(description: text)
			}
			.lineLimit(3, reservesSpace: true)
			.textFieldStyle(.inList)
		}
		
		Section {
			StateTextField(state.location) { text in
			}
			.textFieldStyle(.inList)
			
			TextButton(state.pickLocation, textAlignment: .leading) {
				viewModel.onPickLocationClicked()
			}
		} header : {
			Text(BusinessRes.strings().business_settings_location_title.desc().localized())
		} footer : {
			TextButton(state.testLocation, textAlignment: .leading) {
				viewModel.onTestLocationClick()
			}
		}
		
		Section(BusinessRes.strings().business_settings_address_title.desc().localized()) {
			StateTextField(state.address) { text in
				viewModel.onAddressChanged(address: text)
			}
			.textFieldStyle(.inList)
		}
		
		Section(BusinessRes.strings().business_settings_currency_title.desc().localized()) {
			PickerField(state.currency) { option in
				viewModel.onCurrencySelected(currencyUI: option as! CurrencyUI)
			}
			.textFieldStyle(.inList)
		}
		
		Section(BusinessRes.strings().business_settings_socials_title.desc().localized()) {
			StateTextField(state.instagram) { text in
				viewModel.onInstagramChanged(insta: text)
			}
			.textFieldStyle(.inList)
			
			StateTextField(state.telegram) { text in
				viewModel.onTelegramChanged(telegram: text)
			}
			.textFieldStyle(.inList)
			
			StateTextField(state.viber) { text in
				viewModel.onViberChanged(viber: text)
			}
			.textFieldStyle(.inList)
		}
	}
}

#Preview {
	NavigationStack {
		BusinessSettingsScreen(id: shared.KotlinUuid.companion.random())
	}
}
