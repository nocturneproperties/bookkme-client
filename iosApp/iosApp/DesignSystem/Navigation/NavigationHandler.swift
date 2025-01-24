//
//  NavigationHandler.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 25.01.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import Combine
import shared

struct NavigationHandler: ViewModifier {
    
    @ObservedObject
    var navigationState :IOSNavigationState
    @State
    var handler: (any NavigationDestination) -> Void
    
    func body(content: Content) -> some View {
        content
            .onReceive(navigationState.publisher) { navigation in
                if let destination = navigation {
                    handler(destination)
                    navigationState.navigationDestination = nil
                }
            }
    }
}

extension View {
    func handleNavigation(state: NavigationState, handler: @escaping (any NavigationDestination) -> Void) -> some View {
        modifier(NavigationHandler(navigationState: state.impl(), handler: handler))
    }
}
