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
	
	
	@ObservedObject
	var appBarState: IOSAppBarState
	
	func body(content: Content) -> some View {
		content
			.navigationTitle(appBarState.title.localized())
	}
}

extension View {
	func withNavigationBar(state: AppBarState) -> some View {
		return modifier(AppBarHandler(appBarState: state.impl()))
	}
}
