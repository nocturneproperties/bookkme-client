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
	
	@StateViewModel var viewModel: BusinessSettingsViewModel
	
	init(id: shared.KotlinUuid) {
		self._viewModel = StateViewModel(wrappedValue: IOSBusinessDiKt.businessSettingsVM(id: id))
	}
	
	var body: some View {
		BusinessSettingsContent(viewModel: viewModel)
			.background(AppColors.background)
			.withNavigationBar(viewModel.uiState.appBar)
			.handleNotifications(viewModel.uiState.notifications)
			.sendLifecycleEventsTo(viewModel)
			.toolbar {
				TextButton(state: viewModel.uiState.save) {
					viewModel.onSaveClick()
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
		ScrollView {
			VStack(alignment: .leading, spacing: 16) {
				VStack(alignment: .leading) {
					Header(text: BusinessRes.strings().business_settings_name_title.desc().localized())
					StateTextField(state: state.name) { text in
						viewModel.onNameChanged(name: text)
					}
				}
				VStack(alignment: .leading) {
					Header(text: BusinessRes.strings().business_settings_description_title.desc().localized())
					StateTextField(state: state.description_, textEditor: true) { text in
						viewModel.onDescriptionChanged(description: text)
					}
					.lineLimit(3, reservesSpace: true)
				}
				VStack(alignment: .leading) {
					Header(text: BusinessRes.strings().business_settings_location_title.desc().localized())
					HStack(spacing: 0) {
						StateTextField(state: state.location) { text in
						}
						
						TextButton(state: state.pickLocation, maxWidth: nil) {
							viewModel.onPickLocationClicked()
						}.padding(.horizontal)
					}
					TextButton(state: state.testLocation, textAlignment: .leading) {
						viewModel.onTestLocationClick()
					}
				}
				VStack(alignment: .leading) {
					Header(text: BusinessRes.strings().business_settings_address_title.desc().localized())
					StateTextField(state: state.address) { text in
						viewModel.onAddressChanged(address: text)
					}
				}
				VStack(alignment: .leading) {
					Header(text: BusinessRes.strings().business_settings_currency_title.desc().localized())
					PickerField(state: state.currency) { option in
						viewModel.onCurrencySelected(currencyUI: option as! CurrencyUI)
					}
				}
				VStack(alignment: .leading) {
					Header(text: BusinessRes.strings().business_settings_socials_title.desc().localized())
					StateTextField(state: state.instagram) { text in
						viewModel.onInstagramChanged(insta: text)
					}
					StateTextField(state: state.telegram) { text in
						viewModel.onTelegramChanged(telegram: text)
					}
					StateTextField(state: state.viber) { text in
						viewModel.onViberChanged(viber: text)
					}
				}
			}.padding()
		}
	}
}

#Preview {
	NavigationStack {
		BusinessSettingsScreen(id: shared.KotlinUuid.companion.random())
	}
}
