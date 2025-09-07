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
    
    @ObservedObject
    var state: IOSTextFieldState
	@State
	var isEditor:Bool = false
    @State
    var onTextChanged: (String) -> Void
	
	init(state: TextFieldState, textEditor: Bool = false, onTextChanged: @escaping (String) -> Void) {
		self.state = state.impl()
		self.isEditor = textEditor
		self.onTextChanged = onTextChanged
	}
    
    var body: some View {
        VStack {
			HStack {
				if (state.startIcon == nil) {
					Image(systemName: "key.fill")
						.frame(width: 24, height: 24)
				}
				LabeledContent {
					TextField(
						state.hint.localized(),
						text: Binding<String>(
							get: { state.text },
							set: { text in
								withAnimation {
									let newValue = String(text.prefix(Int(state.maxLength)))
									if (newValue != state.text) {
										onTextChanged(String(text.prefix(Int(state.maxLength)))) }
								}
							}
						),
						axis: isEditor ? .vertical : .horizontal
					)
					.font(Font.system(.body))
					.disabled(!state.enabled)
				} label: {
					if (!state.label.localized().isEmpty) {
						Text(state.label.localized())
							.frame(minWidth: 100, alignment: .leading)
					}
				}
			}
			.padding(.leading, state.startIcon == nil ? 12 : 6)
			.padding(.trailing)
            .padding(.vertical, 12)
            .background(AppColors.elevated)
            .overlay(
                state.isError ?
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
					.foregroundStyle(state.isError ? AppColors.error : AppColors.secondary)
            }
        }
    }
}

#Preview {
    
    @Previewable
    @State
	var value: IOSTextFieldState = IOSTextFieldState(enabled: true, supportingTextRes: RawStringDesc(string: "Error") , hint: RawStringDesc(string: "Hint"), isError: false, isValid: true, maxLength: 20, readOnly: false, text: "Text")
    
	StateTextField(state: value) { _ in
		
	}
}
