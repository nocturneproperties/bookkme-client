//
//  SignUpScreen.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 25.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct SignUpScreen: View {
    
    @StateObject var signUpVM: SignUpViewModel = AuthDiKt.signUpVM()
    
    var body: some View {
        VStack {
            StateTextField(state: signUpVM.uiState.name.impl()) { text in
                signUpVM.onFirstNameTextChanged(text: text)
            }
            .textContentType(.givenName)
            
            StateTextField(state: signUpVM.uiState.lastName.impl()) { text in
                signUpVM.onLastNameTextChanged(text: text)
            }
            .textContentType(.familyName)
            
            StateTextField(state: signUpVM.uiState.email.impl()) { text in
                signUpVM.onEmailTextChanged(text: text)
            }
            .textContentType(.emailAddress)
            
            Spacer()
            
            StateButton(state: signUpVM.uiState.confirmButton.impl())
        }.padding()
    }
}

#Preview {
    KMMPreviewView {
        SignUpScreen()
    }
}
