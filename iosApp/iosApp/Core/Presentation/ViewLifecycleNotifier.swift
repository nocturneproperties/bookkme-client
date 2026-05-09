//
//  ViewLifecycleNotifier.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 29.01.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct ViewLifecycleNotifier: ViewModifier {
    
    var viewModel: shared.ViewModel
    
    func body(content: Content) -> some View {
        content
            .onAppear {
                viewModel.onViewPresented()
            }
            .onDisappear {
                viewModel.onViewHidden()
            }
    }
}

extension View {
    func sendLifecycleEventsTo(_ viewModel: shared.ViewModel) -> some View {
        modifier(ViewLifecycleNotifier(viewModel: viewModel))
    }
}
