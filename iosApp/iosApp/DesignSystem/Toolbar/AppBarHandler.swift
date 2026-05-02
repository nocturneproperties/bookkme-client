//
//  AppBarHandler.swift
//  iosApp
//
//  Created by BookkMe on 01.06.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct AppBarHandler: ViewModifier {
	
	
	@Environment(\.dismiss) var dismiss
	var appBarState: IOSAppBarState
	
	var displayMode: NavigationBarItem.TitleDisplayMode {
		switch appBarState.size_ {
		case .large:
				.large
		case .small:
				.inline
		default : .large
		}
	}
	
	func body(content: Content) -> some View {
		content
			.navigationBarBackButtonHidden(true)
			.navigationBarTitleDisplayMode(displayMode)
			.navigationTitle(appBarState.title.localized())
			.toolbar {
				ToolbarItem(placement: .navigation) {
					Button {
						(appBarState.onBackClick ?? { dismiss() })()
					} label: {
						Label("Back", systemImage: "chevron.left")
					}
				}
				if (!appBarState.actions.items.isEmpty) {
					ToolbarItem(placement: .topBarTrailing) {
						HStack {
							ForEach(appBarState.actions.items(AppBarAction.self), id: \.self) { action in
								Button(action: action.onClick) {
									Image(resource: action.icon)
										.renderingMode(.template)
										.foregroundStyle(AppColors.actionText)
										.frame(width: 44, height: 44)
								}
								.accessibilityLabel(action.contentDescription?.localized() ?? "")
								.buttonStyle(.plain)
							}
						}
					}
				}
			}
	}
}

extension View {
	func withNavigationBar(state: AppBarState) -> some View {
		return modifier(AppBarHandler(appBarState: state.impl()))
	}
}
