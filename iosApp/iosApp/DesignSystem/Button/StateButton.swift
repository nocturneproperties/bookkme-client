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
	let textAlignment: Alignment
	let onClick: (() -> Void)?
    
	init(_ state: IOSButtonState, textAlignment: Alignment = .center, onClick: (() -> Void)? = nil) {
		self._state = Bindable(wrappedValue: state)
		self.textAlignment = textAlignment
		self.onClick = onClick
	}
	
	init(_ state: ButtonState, textAlignment: Alignment = .center, onClick: (() -> Void)? = nil) {
		self._state = Bindable(wrappedValue: state.impl())
		self.textAlignment = textAlignment
		self.onClick = onClick
	}
	
    var body: some View {
		Button(action: {
			withAnimation {
				self.onClick?()
				self.state.onClick?()
			}
		}) {
			ZStack {
				ProgressView()
					.opacity(state.isLoading ? 1 : 0)
				HStack {
					if let icon = state.icon {
						Image(resource: icon)
					}
					Text(state.text.localized())
						.frame(alignment: textAlignment)
				}
				.frame(maxWidth: .infinity, alignment: textAlignment)
				.opacity(state.isLoading ? 0 : 1)
			}
			.contentShape(Rectangle())
			.animation(.default, value: state.isLoading)
        }
        .disabled(!state.isEnabled)
    }
}

struct StateButton: View {
	@Bindable
    var state: IOSButtonState
    let onClick: (() -> Void)?
    
	init(_ state: IOSButtonState, onClick: (() -> Void)? = nil) {
		self._state = Bindable(wrappedValue: state)
		self.onClick = onClick
	}
	
	init(_ state: ButtonState, onClick: (() -> Void)? = nil) {
		self._state = Bindable(wrappedValue: state.impl())
		self.onClick = onClick
	}
	
    var body: some View {
		Button(
			action: {
				withAnimation {
					onClick?()
					state.onClick?()
				}
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
	let maxWidth: CGFloat? = .infinity
	let icon: String
	let onClick: () -> Void
	
	init(state: IOSButtonState, maxWidth: CGFloat? = .infinity, icon: String, onClick: @escaping () -> Void) {
		self._state = Bindable(wrappedValue: state)
		self.onClick = onClick
		self.icon = icon
	}
	
	init(state: ButtonState, maxWidth: CGFloat? = .infinity, icon: String, onClick: @escaping () -> Void) {
		self._state = Bindable(wrappedValue: state.impl())
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

struct TextStandaloneButton: ButtonStyle {
	func makeBody(configuration: Configuration) -> some View {
		configuration.label
			.buttonStyle(.plain)
			.frame(maxWidth: .infinity, minHeight: 48)
			.foregroundStyle(AppColors.actionText)
	}
}

struct TextInListButton: ButtonStyle {
	func makeBody(configuration: Configuration) -> some View {
		configuration.label
			.buttonStyle(.plain)
			.frame(maxWidth: .infinity)
			.foregroundStyle(AppColors.actionText)
	}
}

struct ActionTextButton: ButtonStyle {
	func makeBody(configuration: Configuration) -> some View {
		configuration.label
			.buttonStyle(.plain)
			.foregroundStyle(AppColors.actionText)
	}
}

struct NegativeTextButton: ButtonStyle {
	func makeBody(configuration: Configuration) -> some View {
		configuration.label
			.buttonStyle(.plain)
			.foregroundStyle(AppColors.error)
	}
}

extension ButtonStyle where Self == TextInListButton {
	static var textInList: TextInListButton { TextInListButton() }
}

extension ButtonStyle where Self == TextStandaloneButton {
	static var textStandalone: TextStandaloneButton { TextStandaloneButton() }
}

extension ButtonStyle where Self == ActionTextButton {
	static var textAction: ActionTextButton { ActionTextButton() }
}

extension ButtonStyle where Self == NegativeTextButton {
	static var negativeAction: NegativeTextButton { NegativeTextButton() }
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
