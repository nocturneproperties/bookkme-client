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
	
	var publisher: AnyPublisher<NavigationDestination, Never> {
		$navigationDestination.flatMap(\.publisher)
			.eraseToAnyPublisher()
	}
	
	func push(destination: NavigationDestination) {
		navigationDestination.append(destination)
	}
	
	func removeFirst() {
		navigationDestination.removeFirst()
	}
}

extension shared.NavigationState {
    func impl() -> IOSNavigationState {
        return self as! IOSNavigationState
    }
}
