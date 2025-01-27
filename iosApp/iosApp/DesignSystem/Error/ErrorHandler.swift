//
//  ErrorHandler.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 24.01.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import Combine
import shared

struct ErrorHandler: ViewModifier {
    
    @ObservedObject
    var errorState :IOSErrorState
    

    @State var showAlert: Bool = false
    @State var presenting: PresentationErrorMessage? = nil
    
    func body(content: Content) -> some View {
        content
            .onReceive(errorState.publisher) { value in
                switch value {
                case is PresentationErrorMessage:
                    presenting = value as? PresentationErrorMessage
                    showAlert = true
                    break
                case is PresentationErrorIgnore:
                    errorState.removeFirst()
                    break
                default:
                    errorState.removeFirst()
                    break
                }
            }
            .alert(presenting?.title?.localized() ?? "", isPresented:$showAlert, presenting:$presenting) { details in
                Button(role: .cancel) {
                    showAlert = false
                    errorState.removeFirst()
                } label: {
                    Text(presenting?.buttonText.localized() ?? "")
                }
            } message: { details in
                if let data = presenting {
                    Text(data.message.localized())
                }
            }
    }
}

extension View {
    func handleErrors(state: ErrorState) -> some View {
        modifier(ErrorHandler(errorState: state.impl()))
    }
}
