//
//  StateButton.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 26.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct TextButton: View {
    @ObservedObject
    var state: IOSButtonState
    @State
    var onClick: () -> Void = {}
    
    var body: some View {
        Button(action: onClick) {
            if (state.isLoading) {
                ProgressView()
                    .frame(maxWidth: .infinity, minHeight: 36)
            } else {
                Text(state.text.localized())
                    .frame(maxWidth: .infinity, minHeight: 36)
            }
        }
        .disabled(!state.isEnabled)
    }
}

struct StateButton: View {
    @ObservedObject
    var state: IOSButtonState
    @State
    var onClick: () -> Void = {}
    
    var body: some View {
        Button(action: onClick) {
            if (state.isLoading) {
                ProgressView()
                    .frame(maxWidth: .infinity, minHeight: 36)
            } else {
                Text(state.text.localized())
                    .frame(maxWidth: .infinity, minHeight: 36)
            }
        }
        .disabled(!state.isEnabled)
        .buttonStyle(.borderedProminent)
    }
}

#Preview {
    
    @Previewable
    @State
    var value: IOSButtonState = IOSButtonState(text: RawStringDesc(string: "Button"))
    VStack {
        StateButton(state: value)
    }
}
