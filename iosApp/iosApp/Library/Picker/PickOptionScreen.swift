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
	
	@Environment(\.dismiss) var dismiss
	@StateViewModel var viewModel: PickOptionViewModel
	
	let onItemsSelected: ([KeyValueData]) -> Void
	
	init(args: PickerScreenArgs, onItemsSelected: @escaping ([KeyValueData]) -> Void) {
		_viewModel = StateViewModel(wrappedValue: IOSPickerDiKt.pickOptionVM(args: args))
		self.onItemsSelected = onItemsSelected
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
		.overlay(alignment: .bottomTrailing) {
			SelectAllButton(state: uiState.selectAllButton.impl())
		}
		.toolbar {
			if uiState.selectButton.isVisible {
				TextButton(uiState.selectButton)
			}
		}
		.withNavigationBar(uiState.appBar)
		.sendLifecycleEventsTo(viewModel)
		.handleNavigation(uiState.navigation) { event in
			switch event {
			case let event as PickerNavigationDestination.FinishWithResult:
				onItemsSelected(event.pickResults)
				break  
			case is PickerNavigationDestination.Back:
				dismiss()
				break
			default :
				break
			}
		}
	}
}

struct SelectAllButton: View {
	
	@Bindable var state: IOSButtonState
	
	var body: some View {
		if state.isVisible {
			Button {
				withAnimation {
					state.onClick?()
				}
			} label: {
				Text(state.text.localized())
					.font(.subheadline.weight(.semibold))
					.padding(.horizontal, 20)
					.padding(.vertical, 14)
			}
			.foregroundStyle(AppColors.actionText)
			.background(AppColors.elevated, in: Capsule())
			.shadow(color: .black.opacity(0.15), radius: 6, y: 2)
			.padding(16)
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
