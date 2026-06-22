//
//  StateTextField.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 26.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct StateTextField: View {
    
    @Bindable
    var state: IOSTextFieldState
	let isEditor: Bool
    let onTextChanged: ((String) -> Void)? //TODO: Backward compatibility, remove when deprecated screens will be refactored
	
	private var keyboardType: UIKeyboardType {
		switch state.inputType {
		case .digit:
			return .numberPad
		case .decimal:
			return .decimalPad
		case .ascii:
			return .asciiCapable
		case .email:
			return .emailAddress
		case .phone:
			return .phonePad
		case .password:
			return .asciiCapable
		default:
			return .default
		}
	}
	
	init(
		_ state: TextFieldState,
		textEditor: Bool = false,
		onTextChanged: ((String) -> Void)? = nil
	) {
		self._state = Bindable(wrappedValue: IOSTextFieldState.cast(state))
		self.isEditor = textEditor
		self.onTextChanged = onTextChanged
	}
    
    var body: some View {
        VStack {
			HStack {
				if (state.startIcon != nil) {
					Image(uiImage: state.startIcon!.toUIImage()!)
						.frame(width: 24, height: 24)
				}
				ZStack(alignment: .trailingLastTextBaseline) {
					LabeledContent {
						TextField(
							state.placeholder.localized(),
							text: Binding<String>(
								get: { state.text },
								set: { text in
									withAnimation {
										let newValue = String(text.prefix(Int(state.maxLength)))
										if (newValue != state.text) {
											state.onTextChanged?(newValue)
											onTextChanged?(newValue)
										}
									}
								}
							),
							axis: isEditor ? .vertical : .horizontal
						)
						.keyboardType(keyboardType)
						.font(Font.system(.body))
						.disabled(!state.enabled || state.readOnly)
					} label: {
						if (!state.label.localized().isEmpty) {
							Text(state.label.localized())
								.frame(minWidth: 100, alignment: .leading)
								.lineLimit(1)
						}
					}
				}
				if let suffix = state.suffix {
					Text(suffix.localized())
						.font(.footnote)
						.foregroundStyle(AppColors.secondary)
				}
			}
            .overlay(
				state.validationState == ValidationState.error ?
                RoundedRectangle(cornerRadius: 10)
                    .stroke(
                        AppColors.error,
                        style: StrokeStyle(
                            lineWidth: 3,
                            lineCap: .round,
                            lineJoin: .round
                        )
                    )
                    .frame(width: 20)
                    .mask(
                        LinearGradient(
                            colors: [.black,.clear, .clear, .clear],
                            startPoint: .leading,
                            endPoint: .trailing
                        )
                    ) : nil,
                alignment: .leading
            )
            if let supportingText = state.supportingTextRes {
                Text(supportingText.localized())
                    .frame(maxWidth: .infinity, alignment: .leading)
					.font(.footnote)
                    .scaledToFit()
					.foregroundStyle(state.validationState == ValidationState.error ? AppColors.error : AppColors.secondary)
					.padding(.leading)
            }
        }.id(state.id)
	}
}

struct StandaloneTextFieldStyle: TextFieldStyle {
	func _body(configuration: TextField<Self._Label>) -> some View {
		configuration
			.padding(.horizontal, 8)
			.padding(.vertical, 12)
			.background(AppColors.elevated)
			.cornerRadius(10)
	}
}

struct OnElevatedTextFieldStyle: TextFieldStyle {
	func _body(configuration: TextField<Self._Label>) -> some View {
		configuration
			.padding(.horizontal, 8)
			.padding(.vertical, 12)
			.background(AppColors.background)
			.cornerRadius(10)
	}
}

struct InListTrailingTextFieldStyle: TextFieldStyle {
	func _body(configuration: TextField<Self._Label>) -> some View {
		configuration
			.padding(.horizontal, 0)
			.padding(.vertical, 0)
			.background(Color.clear)
			.multilineTextAlignment(.trailing)
	}
}

struct InListTextFieldStyle: TextFieldStyle {
	func _body(configuration: TextField<Self._Label>) -> some View {
		configuration
			.padding(.horizontal, 0)
			.padding(.vertical, 0)
			.background(Color.red)
	}
}

extension TextFieldStyle where Self == InListTextFieldStyle {
	static var inList: InListTextFieldStyle { InListTextFieldStyle() }
}

extension TextFieldStyle where Self == InListTrailingTextFieldStyle {
	static var inListTrailing: InListTrailingTextFieldStyle { InListTrailingTextFieldStyle() }
}

extension TextFieldStyle where Self == OnElevatedTextFieldStyle {
	static var onElevated: OnElevatedTextFieldStyle { OnElevatedTextFieldStyle() }
}

extension TextFieldStyle where Self == StandaloneTextFieldStyle {
	static var standalone: StandaloneTextFieldStyle { StandaloneTextFieldStyle() }
}

#Preview {
    
    @Previewable
    @State
	var value: IOSTextFieldState = IOSTextFieldState(enabled: true, supportingTextRes: RawStringDesc(string: "Error"), placeholder: RawStringDesc(string: "Hint"), isValid: true, maxLength: 20, readOnly: false, text: "Text",)
    
	VStack {
		StateTextField(value) { _ in
			
		}.padding()
	}.background(AppColors.background)
}
