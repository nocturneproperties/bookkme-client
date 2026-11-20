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
	
	init(state: BooleanState, onToggledChanged: ((Bool) -> Void)? = nil) {
		self._state = Bindable(wrappedValue: IOSBooleanState.cast(state))
		self.onToggledChanged = {
			onToggledChanged?($0)
			state.onCheckedChange?(KotlinBoolean(bool: $0))
		}
	}
	
	var body: some View {
		Toggle(isOn: Binding(
			get: { state.isChecked },
			set: { newValue in
				withAnimation {
					state.isChecked = newValue
					onToggledChanged(newValue)
				}
			}
		)) {
			Text(state.text.localized())
			if let supportingText = state.supportingTextRes {
				Text(supportingText.localized())
					.font(.footnote)
					.foregroundStyle(state.validationState == .error ? AppColors.error : AppColors.secondary)
			}
		}
		.disabled(!state.isEnabled)
	}
}
