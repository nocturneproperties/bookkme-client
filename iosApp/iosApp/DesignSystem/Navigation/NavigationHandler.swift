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
    var navigationState: IOSNavigationState
    @State
    var handler: (NavigationDestination) -> Void
    
    func body(content: Content) -> some View {
        content
            .onChange(of: navigationState.navigationDestination, initial: true) { oldValue, newValue in
                if let destination = newValue {
                    navigationState.navigationDestination = nil
                    handler(destination)
                }
            }
    }
}

extension View {
    func handleNavigation(state: NavigationState, handler: @escaping (NavigationDestination) -> Void) -> some View {
        return modifier(NavigationHandler(navigationState: state.impl(), handler: handler))
    }
}
