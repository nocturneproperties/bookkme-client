//
//  StateSwitch.swift
//  iosApp
//
//  Created by BookkMe on 07.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct StateSwitch: View {
	
	@Bindable
	var state: IOSBooleanState
	let onToggledChanged: (Bool) -> Void
	
	init(state: BooleanState, onToggledChanged: @escaping (Bool) -> Void) {
		self._state = Bindable(wrappedValue: IOSBooleanState.cast(state))
		self.onToggledChanged = onToggledChanged
	}
	
	var body: some View {
		Toggle(state.text.localized(), isOn: Binding(
			get: { state.isChecked },
			set: { newValue in
				state.isChecked = newValue
				onToggledChanged(newValue)
			}
		))
	}
}
