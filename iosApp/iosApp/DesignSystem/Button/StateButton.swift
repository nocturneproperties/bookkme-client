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
    @Bindable
    var state: IOSButtonState
    @State
	var maxWidth: CGFloat?
	@State
	var textAlignment: Alignment = .center
	let onClick: (() -> Void)?
    
	init(_ state: IOSButtonState, maxWidth: CGFloat? = .infinity, textAlignment: Alignment = .center, onClick: (() -> Void)? = nil) {
		self.state = state
		self.textAlignment = textAlignment
		self.onClick = onClick
		self.maxWidth = maxWidth
	}
	
	init(_ state: ButtonState, maxWidth: CGFloat? = .infinity, textAlignment: Alignment = .center, onClick: (() -> Void)? = nil) {
		self.state = state.impl()
		self.textAlignment = textAlignment
		self.onClick = onClick
		self.maxWidth = maxWidth
	}
	
    var body: some View {
		Button(action: {
			self.onClick?()
			self.state.onClick?()
		}) {
			ZStack {
				ProgressView()
					.opacity(state.isLoading ? 1 : 0)
				Text(state.text.localized())
					.frame(alignment: textAlignment)
					.opacity(state.isLoading ? 0 : 1)
			}
			.animation(.default, value: state.isLoading)
        }
		.frame(maxWidth: maxWidth, minHeight: 36)
        .disabled(!state.isEnabled)
    }
}

struct StateButton: View {
	@Bindable
    var state: IOSButtonState
    let onClick: (() -> Void)?
    
	init(_ state: IOSButtonState, onClick: (() -> Void)? = nil) {
		self.state = state
		self.onClick = onClick
	}
	
	init(_ state: ButtonState, onClick: (() -> Void)? = nil) {
		self.state = state.impl()
		self.onClick = onClick
	}
	
    var body: some View {
		Button(
			action: {
				onClick?()
				state.onClick?()
			}
		) {
			if (state.isLoading) {
				ProgressView()
					.tint(AppColors.primary)
					.frame(maxWidth: .infinity, minHeight: 36)
			} else {
				Text(state.text.localized())
					.frame(maxWidth: .infinity, minHeight: 36)
			}
		}
        .disabled(!state.isEnabled)
		.tint(state.isEnabled ? AppColors.buttonActive : AppColors.buttonInactive)
        .buttonStyle(.borderedProminent)
    }
}

struct IconButton: View {
	@Bindable
	var state: IOSButtonState
	@State
	var maxWidth: CGFloat? = .infinity
	@State
	var icon: String
	let onClick: () -> Void
	
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
        StateButton(value) {
            
        }
		TextButton(value) {
			
		}
    }
}
