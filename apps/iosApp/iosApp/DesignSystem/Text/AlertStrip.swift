//
//  AlertStrip.swift
//  iosApp
//
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct AlertStrip: View {

	var text: any StringDesc
	var action: any ButtonState
	var icon: shared.ImageResource

	var body: some View {
		HStack(alignment: .center, spacing: 8) {
			Image(resource: icon)
				.renderingMode(.template)
				.resizable()
				.scaledToFit()
				.frame(width: 16, height: 16)
				.foregroundStyle(AppColors.error)
				.accessibilityHidden(true)
			Text(text.localized())
				.font(.footnote)
				.fontWeight(.medium)
				.foregroundStyle(AppColors.error)
				.lineLimit(1)
				.truncationMode(.tail)
				.frame(maxWidth: .infinity, alignment: .leading)
			actionView
		}
		.padding(.leading, 16)
		.padding(.trailing, 8)
		.frame(minHeight: 44)
		.frame(maxWidth: .infinity)
		.background(AppColors.error.opacity(0.12))
		.background(AppColors.background)
		.overlay(alignment: .top) {
			Rectangle()
				.fill(AppColors.error.opacity(0.2))
				.frame(height: 1)
		}
	}

	@ViewBuilder
	private var actionView: some View {
		if action.isLoading {
			ProgressView()
				.controlSize(.small)
				.tint(AppColors.error)
				.padding(.horizontal, 14)
		} else {
			Button {
				action.onClick?()
			} label: {
				Text(action.text.localized())
					.font(.footnote)
					.fontWeight(.bold)
					.foregroundStyle(AppColors.error)
					.padding(.horizontal, 8)
					.frame(minHeight: 44)
			}
			.disabled(!action.isEnabled)
		}
	}
}
