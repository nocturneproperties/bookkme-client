//
//  MainStack.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 29.01.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct MainStack: View {
    
    @StateObject var navigationStack = NavigationStackHolder()
    
    var body: some View {
        NavigationStack(path: $navigationStack.path) {
            Text("TODO MAIN")
        }
    }
}
