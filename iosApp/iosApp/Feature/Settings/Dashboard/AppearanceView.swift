//
//  AppearanceView.swift
//  iosApp
//
//  Created by BookkMe on 09.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared


struct AppearanceView: View {
	
	let state: IOSAppearanceSection
	
	let onSchemeSelected: (AppearanceSectionUIColorScheme) -> Void
	
	
	init(state: AppearanceSection, onSchemeSelected: @escaping (AppearanceSectionUIColorScheme) -> Void) {
		self.state = state.impl()
		self.onSchemeSelected = onSchemeSelected
	}
	
	var body: some View {
			HStack(spacing: 24) {
				ColorSchemeItemView(
					schemeExpect: .dark,
					schemeActual: state.colorScheme,
					fill: Gradient(colors: [.black]),
					onSchemeSelected: onSchemeSelected
				)
				
				ColorSchemeItemView(
					schemeExpect: .light,
					schemeActual: state.colorScheme,
					fill: Gradient(colors: [.white]),
					onSchemeSelected: onSchemeSelected
				)
	
				ColorSchemeItemView(
					schemeExpect: .system,
					schemeActual: state.colorScheme,
					fill: LinearGradient(
						gradient: Gradient(stops: [
							Gradient.Stop(color: .black, location: 0.5),
							Gradient.Stop(color: .white, location: 0.5)
						]),
						startPoint: .leading,
						endPoint: .trailing
					),
					onSchemeSelected: onSchemeSelected
				)

			}
			.padding()
			.frame(maxWidth: .infinity)
	}
}


struct ColorSchemeItemView<S>: View where S: ShapeStyle {
	
	let schemeExpect: AppearanceSectionUIColorScheme
	let schemeActual: AppearanceSectionUIColorScheme
	let fill: S
	let onSchemeSelected: (AppearanceSectionUIColorScheme) -> Void
	
	var body: some View {
		VStack {
			RoundedRectangle(cornerRadius: 10)
				.fill(fill)
				.strokeBorder(
					schemeExpect == schemeActual ? AppColors.actionText : AppColors.header,
					lineWidth: schemeExpect == schemeActual ? 4 : 0.5)
				.frame(width: 70, height: 70)
				.onTapGesture {
					onSchemeSelected(schemeExpect)
				}
			Text(schemeExpect.title.localized())
				.font(.caption)
		}
	}
}
