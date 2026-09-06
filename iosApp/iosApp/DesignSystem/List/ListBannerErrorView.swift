//
//  ListBannerErrorView.swift
//  iosApp
//
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct ListBannerErrorView: View {

	var state: BannerErrorState

	var body: some View {
		HStack(alignment: .center, spacing: 8) {
			Image(systemName: "exclamationmark.triangle")
				.font(.system(size: 14))
				.foregroundStyle(AppColors.error)
			Text(state.text.localized())
				.font(.caption)
				.foregroundStyle(AppColors.error)
				.frame(maxWidth: .infinity, alignment: .leading)
			Button {
				state.onRetryClick()
			} label: {
				Text(DesignSystem.strings().action_retry.desc().localized())
					.font(.caption)
					.fontWeight(.semibold)
					.foregroundStyle(AppColors.error)
			}
		}
		.padding(.horizontal, 16)
		.padding(.vertical, 12)
		.frame(maxWidth: .infinity)
		.background(AppColors.error.opacity(0.12))
	}
}
