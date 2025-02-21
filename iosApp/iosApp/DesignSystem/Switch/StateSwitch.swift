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
	
	@ObservedObject
	var state: IOSSwitchState
	
	let onToggledChanged: (Bool) -> Void
	
	init(state: SwitchState, onToggledChanged: @escaping (Bool) -> Void) {
		self.state = state.impl()
		self.onToggledChanged = onToggledChanged
	}
	
	var body: some View {
		Toggle(state.text.localized(), isOn: $state.isChecked)
			.onChange(of: state.isChecked, initial: false) { _, newValue in
				onToggledChanged(newValue)
			}
	}
}
