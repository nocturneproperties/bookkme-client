//
//  StateCheckBox.swift
//  iosApp
//
//  Created by BookkMe on 29.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct StateCheckBox: View {
	
	@Bindable var state: IOSCheckBoxState
	
	init(_ state: IOSCheckBoxState) {
		self._state = Bindable(wrappedValue: state)
	}
	
	init(_ state: CheckBoxState) {
		self._state = Bindable(wrappedValue: IOSCheckBoxState.cast(state))
	}
	
	var body: some View {
		Toggle(state.text.localized(), isOn: Binding(get: { state.isChecked }, set: { state.onCheckedChange?(KotlinBoolean(bool: $0)) }))
			.disabled(!state.isEnabled)
	}
}

#Preview {
	let state = IOSCheckBoxState(text: RawStringDesc(string: "Text"))
	StateCheckBox(state)
}
