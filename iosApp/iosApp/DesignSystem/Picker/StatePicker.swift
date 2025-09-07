//
//  IOSPicker.swift
//  iosApp
//
//  Created by BookkMe on 06.09.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct StatePicker<T: PickerPresentation>: View {
	
	@ObservedObject
	var state: IOSPickerState
	
	@State
	var onOptionPicked: (T) -> Void
	
	init (state: PickerFieldState, onOptionPicked: @escaping (T) -> Void) {
		self.state = state.impl()
		self.onOptionPicked = onOptionPicked
	}
	var body: some View {
		LabeledContent {
			Picker(
				state.text.localized(),
				selection: Binding(
					get: { state.selectedItem },
					set: { option in
						onOptionPicked(option as! T)
					}
				)
			) {
				ForEach(state.options, id: \.pickerItemId) { option in
					Text(option.displayName.localized()).tag(option)
				}
			}
			.pickerStyle(.automatic)
		} label : {
			Text(state.text.localized())
				.frame(minWidth: 100, alignment: .leading)
		}
		.padding(.horizontal)
		.frame(maxWidth: .infinity, minHeight: 48)
		.background(AppColors.elevated)
		.cornerRadius(10)
	}
}

#Preview {
	@Previewable
	@State
	var value: IOSPickerState = IOSPickerState(
		options: [
			MinimalPickerPresentation(pickerItemId: 1, displayName: RawStringDesc(string: "First")),
			MinimalPickerPresentation(pickerItemId: 2, displayName: RawStringDesc(string: "Second")),
			MinimalPickerPresentation(pickerItemId: 3, displayName: RawStringDesc(string: "Third")),
			MinimalPickerPresentation(pickerItemId: 4, displayName: RawStringDesc(string: "Fourth"))
		],
		selectedItem: MinimalPickerPresentation(pickerItemId: 2, displayName: RawStringDesc(string: "Second")),
		text: RawStringDesc(string: "Select an option")
	)
	
	StatePicker(state: value) { option in
		value.selectedItem = option
	}
}
