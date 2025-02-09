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
    var maxWidth: CGFloat? = .infinity
    @State
    var onClick: () -> Void
    
	init(state: IOSButtonState, maxWidth: CGFloat? = .infinity, onClick: @escaping () -> Void) {
		self.state = state
		self.onClick = onClick
	}
	
	init(state: ButtonState, maxWidth: CGFloat? = .infinity, onClick: @escaping () -> Void) {
		self.state = state.impl()
		self.onClick = onClick
	}
	
    var body: some View {
        Button(action: onClick) {
            if (state.isLoading) {
                ProgressView()
                    .frame(maxWidth: maxWidth, minHeight: 36)
            } else {
                Text(state.text.localized())
                    .frame(maxWidth: maxWidth, minHeight: 36)
            }
        }
        .disabled(!state.isEnabled)
    }
}

struct StateButton: View {
    @ObservedObject
    var state: IOSButtonState
    @State
    var onClick: () -> Void
    
	init(state: IOSButtonState, onClick: @escaping () -> Void) {
		self.state = state
		self.onClick = onClick
	}
	
	init(state: ButtonState, onClick: @escaping () -> Void) {
		self.state = state.impl()
		self.onClick = onClick
	}
	
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
        StateButton(state: value) {
            
        }
    }
}
