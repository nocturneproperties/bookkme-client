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
	let bgColor: Color
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
		bgColor: Color = AppColors.elevated,
		textEditor: Bool = false,
		onTextChanged: ((String) -> Void)? = nil
	) {
		self._state = Bindable(wrappedValue: IOSTextFieldState.cast(state))
		self.isEditor = textEditor
		self.bgColor = bgColor
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
						}
					}
				}
				if let suffix = state.suffix {
					Text(suffix.localized())
						.font(.footnote)
						.foregroundStyle(AppColors.secondary)
				}
			}
			.padding(.horizontal, 8)
            .padding(.vertical, 12)
            .background(bgColor)
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
            
            .cornerRadius(10)
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

#Preview {
    
    @Previewable
    @State
	var value: IOSTextFieldState = IOSTextFieldState(enabled: true, supportingTextRes: RawStringDesc(string: "Error"), placeholder: RawStringDesc(string: "Hint"), isValid: true, maxLength: 20, readOnly: false, text: "Text",)
    
	VStack {
		StateTextField(value) { _ in
			
		}.padding()
	}.background(AppColors.background)
}
