//
//  IOSBootstrapState.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 29.01.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared

class IOSBootstrapState: BootstrapState, ObservableObject {
	
	@Published
	var startDestination: BootstrapNavigationDestination? = nil
	
	@Published
	var colorScheme: BootstrapStateUIColorScheme = BootstrapStateUIColorScheme.system
	
    var navigation: any NavigationState = IOSNavigationState()

}

extension BootstrapState {
	func impl() -> IOSBootstrapState {
		return self as! IOSBootstrapState
	}
}
