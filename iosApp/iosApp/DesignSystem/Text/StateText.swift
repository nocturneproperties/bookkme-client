//
//  StateText.swift
//  iosApp
//
//  Created by BookkMe on 07.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct StateText: View {
	
	@ObservedObject
	var state: IOSTextState
	
	init(state: TextState) {
		self.state = state.impl()
	}
	
	var body: some View {
		if (state.isVisible) {
			Text(state.text.localized())
		}
	}
}
