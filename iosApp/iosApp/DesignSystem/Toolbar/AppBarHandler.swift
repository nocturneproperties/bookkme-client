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
	
	var actions: some View {
		HStack {
			ForEach(appBarState.actions.items(AppBarAction.self), id: \.self) { action in
				Button(action: action.onClick) {
					if let icon = action.icon {
						Image(resource: icon)
							.renderingMode(.template)
							.foregroundStyle(AppColors.actionText)
							.frame(width: 44, height: 44)
					} else {
						Text(action.contentDescription.localized())
					}
				}
				.accessibilityLabel(action.contentDescription.localized())
				.buttonStyle(.plain)
			}
		}
	}
	
	func body(content: Content) -> some View {
		content
			.navigationBarBackButtonHidden(true)
			.navigationTitle(appBarState.title.localized())
			.navigationBarTitleDisplayMode(displayMode)
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
						actions
					}
				}
			}
	}
}

extension View {
	func withNavigationBar(_ state: AppBarState) -> some View {
		return modifier(AppBarHandler(appBarState: state.impl()))
	}
}
