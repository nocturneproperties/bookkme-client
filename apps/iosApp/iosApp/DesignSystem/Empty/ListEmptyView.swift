//
//  EmptyView.swift
//  iosApp
//
//  Created by BookkMe on 03.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct ListEmptyView: View {

	var state: EmptyState

	var body: some View {
		ContentUnavailableView {
			Label(state.label.localized(), systemImage: "tray")
		}
	}
}
