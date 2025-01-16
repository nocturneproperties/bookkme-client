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
    var onTextChanged: (String) -> Void = {_ in }
    
    var body: some View {
        VStack {
            LabeledContent {
                ZStack {
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
                        )
                    )
                    .font(Font.system(.body))
                    .disabled(!state.enabled)
                }
            } label: {
                Text(state.hint.localized())
                    .frame(minWidth: 100, alignment: .leading)
            }
            .padding(.horizontal)
            .padding(.vertical, 12)
            .background(Color(AppColors.elevated))
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
            if let error = state.errorTextRes {
                Text(error.localized())
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .scaledToFit()
                    .foregroundStyle(AppColors.error)
            }
        }
    }
}

#Preview {
    
    @Previewable
    @State
    var value: IOSTextFieldState = IOSTextFieldState(enabled: true, errorTextRes: RawStringDesc(string: "Error") , hint: RawStringDesc(string: "Hint"), isError: false, isValid: true, maxLength: 20, readOnly: false, text: "Text")
    
    StateTextField(state: value)
}
