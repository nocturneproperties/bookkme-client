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
		ZStack(alignment: .center) {
			VStack(alignment: .center, spacing: 20) {
				Image(resource: state.image)
				Text(state.label.localized())
					.font(.subheadline)
					.foregroundStyle(AppColors.secondary)
					.frame(maxWidth: .infinity)
			}.frame(maxWidth: .infinity, minHeight: 400, maxHeight: .infinity)
		}
	}
}
