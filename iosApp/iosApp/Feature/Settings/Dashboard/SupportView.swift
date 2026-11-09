//
//  SupportView.swift
//  iosApp
//
//  Created by BookkMe on 09.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct SupportView: View {
	
	let state: IOSSupportSection
	
	init(state: SupportSection) {
		self.state = state.impl()
	}
	
	var body: some View {
		Button(action: state.onContactClick) {
			Text(state.contact.text.localized())
		}
	}
}
