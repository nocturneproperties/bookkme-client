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
				.navigationDestination(for: AuthDestination.SignUp.self) { value in
                    SignUpScreen()
                }
				.navigationDestination(for: AuthDestination.SignIn.self) { value in
                    SignInScreen()
                }
				.navigationDestination(for: AuthDestination.Troubleshoot.self) { value in
                    TroubleshootScreen()
                }
        }.environmentObject(navigationStack)
    }
}
