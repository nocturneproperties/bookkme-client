//
//  NavigationState.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 23.01.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared
import Combine

class IOSNavigationState: NavigationState, ObservableObject {
	@Published
	var navigationDestination: [NavigationDestination] = []
	
	func push(destination: NavigationDestination) {
		navigationDestination.append(destination)
	}
	
	func removeFirst() {
		DispatchQueue.main.async {
			self.navigationDestination.removeFirst()
		}
	}
}

extension shared.NavigationState {
    func impl() -> IOSNavigationState {
        return self as! IOSNavigationState
    }
}
