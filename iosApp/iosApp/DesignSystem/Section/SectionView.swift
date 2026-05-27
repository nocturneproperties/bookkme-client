//
//  SectionView.swift
//  iosApp
//
//  Created by BookkMe on 27.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct SectionView: View {
	
	var action: Action
	
	var body: some View {
		HStack {
			Text(action.title.localized())
			Spacer()
			Image(systemName: "chevron.right")
				.font(.subheadline)
				.bold()
				.foregroundStyle(.tertiary)
		}.onTapGesture {
			action.onClick()
		}
	}
}
