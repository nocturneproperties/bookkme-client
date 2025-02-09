//
//  AuthorizationStack.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 29.01.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct AuthorizationView: View {
    
    @StateObject var navigationStack = NavigationStackHolder()
    
    var body: some View {
        NavigationStack(path: $navigationStack.path) {
            SignInScreen()
                .navigationDestination(for: SignUpDestination.self) { value in
                    SignUpScreen()
                }
                .navigationDestination(for: SignInDestination.self) { value in
                    SignInScreen()
                }
                .navigationDestination(for: TroubleshootDestination.self) { value in
                    TroubleshootScreen()
                }
        }.environmentObject(navigationStack)
    }
}
