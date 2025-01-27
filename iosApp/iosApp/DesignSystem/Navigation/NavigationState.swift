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
    var navigationDestination: (any NavigationDestination)? = nil
    
    var publisher: AnyPublisher<(any NavigationDestination)?, Never> {
        $navigationDestination.eraseToAnyPublisher()
    }
}

extension shared.NavigationState {
    func impl() -> IOSNavigationState {
        return self as! IOSNavigationState
    }
}
