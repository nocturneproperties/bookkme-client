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
	let appBarState: IOSAppBarState
	
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
		ForEach(appBarState.actions.items(AppBarAction.self), id: \.self) { action in
			Button(role: buttonRole(from: action.type), action: action.onClick) {
				if let icon = action.icon {
					Label(title: {}, icon: {
						Image(resource: icon)
							.renderingMode(.template)
							.foregroundStyle(AppColors.actionText)
							.frame(width: 44, height: 44)
					})
					.frame(maxHeight: .infinity)
				} else {
					Label(title: {
						Text(action.contentDescription.localized())
					}, icon: {})
					.frame(maxHeight: .infinity)
				}
			}
			.accessibilityLabel(action.contentDescription.localized())
		}
	}
	
	func body(content: Content) -> some View {
		content
			.navigationBarBackButtonHidden(true)
			.navigationTitle(appBarState.title.localized())
			.navigationBarTitleDisplayMode(displayMode)
			.toolbar {
				if let backClick = appBarState.onBackClick {
					ToolbarItem(placement: .navigation) {
						Button {
							backClick()
						} label: {
							Label("Back", systemImage: "chevron.left")
						}
					}
				}
				if (!appBarState.actions.items.isEmpty) {
					ToolbarItemGroup(placement: .topBarTrailing) {
						actions
					}
				}
			}
	}
	
	func buttonRole(from type: ActionType) -> ButtonRole? {
		switch(type) {
		case .confirm:
			if #available(iOS 26.0, *) {
				return .confirm
			} else {
				return nil
			}
		case .negative:
			return .destructive
		case .cancel:
			return .cancel
		default:
			return nil
		}
	}
}

extension View {
	func withNavigationBar(_ state: AppBarState) -> some View {
		return modifier(AppBarHandler(appBarState: state.impl()))
	}
}
