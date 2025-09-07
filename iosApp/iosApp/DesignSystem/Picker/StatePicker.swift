//
//  IOSPicker.swift
//  iosApp
//
//  Created by BookkMe on 06.09.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct StatePicker<T:PickerPresentation>: View {
	
	@ObservedObject
	var state: IOSPickerState<T>
	
	@State
	var onOptionPicked: (T) -> Void
	
	init (state: IOSPickerState<T>, onOptionPicked: @escaping (PickerPresentation) -> Void) {
		self.state = state
		self.onOptionPicked = onOptionPicked
	}
	var body: some View {
		List{
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
		}
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
