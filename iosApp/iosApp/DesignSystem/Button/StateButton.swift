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
    var maxWidth: CGFloat = .infinity
	@State
	var textAlignment: Alignment = .center
    @State
    var onClick: () -> Void
    
	init(state: IOSButtonState, maxWidth: CGFloat? = .infinity, textAlignment: Alignment = .center, onClick: @escaping () -> Void) {
		self.state = state
		self.textAlignment = textAlignment
		self.onClick = onClick
	}
	
	init(state: ButtonState, maxWidth: CGFloat? = .infinity, textAlignment: Alignment = .center, onClick: @escaping () -> Void) {
		self.state = state.impl()
		self.textAlignment = textAlignment
		self.onClick = onClick
	}
	
    var body: some View {
        Button(action: onClick) {
            if (state.isLoading) {
                ProgressView()
                    .frame(maxWidth: maxWidth, minHeight: 36)
            } else {
                Text(state.text.localized())
					.frame(maxWidth: maxWidth, minHeight: 36, alignment: textAlignment)
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

struct IconButton: View {
	@ObservedObject
	var state: IOSButtonState
	@State
	var maxWidth: CGFloat? = .infinity
	@State
	var icon: String
	@State
	var onClick: () -> Void
	
	init(state: IOSButtonState, maxWidth: CGFloat? = .infinity, icon: String, onClick: @escaping () -> Void) {
		self.state = state
		self.onClick = onClick
		self.icon = icon
	}
	
	init(state: ButtonState, maxWidth: CGFloat? = .infinity, icon: String, onClick: @escaping () -> Void) {
		self.state = state.impl()
		self.icon = icon
		self.onClick = onClick
	}
	
	var body: some View {
		Button(action: onClick) {
			if (state.isLoading) {
				ProgressView()
			} else {
				Image(systemName: icon)
			}
		}
		.disabled(!state.isEnabled)
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
