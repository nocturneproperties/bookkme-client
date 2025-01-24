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
    private enum FocusField {
        case name
        case lastName
        case email
    }
    @EnvironmentObject var navigationStack: NavigationStackHolder
    @StateObject var signUpVM: SignUpViewModel = AuthDiKt.signUpVM()
    @FocusState private var focusedField: FocusField?
    
    var body: some View {
        VStack {
            StateTextField(state: signUpVM.uiState.name.impl()) { text in
                signUpVM.onFirstNameTextChanged(text: text)
            }
            .focused($focusedField, equals: .name)
            .textContentType(.givenName)
            .submitLabel(.next)
            .onSubmit {
                focusedField = .lastName
            }
            
            StateTextField(state: signUpVM.uiState.lastName.impl()) { text in
                signUpVM.onLastNameTextChanged(text: text)
            }
            .focused($focusedField, equals: .lastName)
            .textContentType(.familyName)
            .submitLabel(.next)
            .onSubmit {
                focusedField = .email
            }
            
            StateTextField(state: signUpVM.uiState.email.impl()) { text in
                signUpVM.onEmailTextChanged(text: text)
            }
            .autocapitalization(.none)
            .focused($focusedField, equals: .email)
            .textContentType(.emailAddress)
            .keyboardType(.emailAddress)
            .submitLabel(.done)
            
            Spacer()
            
            StateButton(state: signUpVM.uiState.confirmButton.impl()) {
                signUpVM.onConfirmButtonClick()
            }
        }
        .padding()
        .handleErrors(state: signUpVM.uiState.error)
        .onReceive(signUpVM.uiState.navigation.impl().publisher) { value in
            switch value {
            case is SignUpNavigationDestinationBack:
                navigationStack.path.removeLast()
                break
            case is SignUpNavigationDestinationToMain:
                navigationStack.path.append(SignInDestination())
                break
            default: break
            }
        }
        .onAppear {
            focusedField = .name
        }
        .onDisappear {
            signUpVM.clear()
        }
    }
}

#Preview {
    SignUpScreen()
}
