//
//  IOSPickOptionScreen.swift
//  iosApp
//
//  Created by BookkMe on 29.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct PickOptionScreen: View {
	
	@StateViewModel var viewModel: PickOptionViewModel
	
	let onItemSelected: (KeyValueData) -> Void
	
	init(args: PickerScreenArgs, onItemSelected: @escaping (KeyValueData) -> Void) {
		_viewModel = StateViewModel(wrappedValue: IOSPickerDiKt.pickOptionVM(args: args))
		self.onItemSelected = onItemSelected
	}
	
	var body: some View {
		let uiState = IOSPickOptionState.cast(viewModel.uiState)
		let listState = IOSListState<IOSPickOptionItem>.cast(uiState.filteredOptions)
		ListGroup(listState: listState, listStyle: .automatic) { option in
			PickOptionItemView(item: option)
		}
		.searchable(
			text: uiState.queryField.binding(),
			placement: .navigationBarDrawer(displayMode: .always),
			prompt: uiState.queryField.placeholder.localized()
		)
		.withNavigationBar(uiState.appBar)
		.sendLifecycleEventsTo(viewModel)
		.handleNavigation(uiState.navigation) { event in
			switch event {
			case let event as PickerNavigationDestination.FinishWithResult:
				onItemSelected(event.pickResult)
				break
			default :
				break
			}
		}
	}
}

struct PickOptionItemView: View {
	
	let item: IOSPickOptionItem
	
	var body: some View {
		HStack {
			Text(item.checkBox.text.localized())
			Spacer()
			if (item.checkBox.isChecked) {
				Image(systemName: "checkmark")
					.foregroundStyle(AppColors.actionText)
			}
		}
		.contentShape(Rectangle())
		.onTapGesture {
			item.checkBox.onCheckedChange?(KotlinBoolean(bool: !item.checkBox.isChecked))
		}
	}
}
