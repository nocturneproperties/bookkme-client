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
        let uiState = signUpVM.uiState
        VStack {
            ScrollView {
                VStack {
                    StateTextField(state: uiState.name.impl()) { text in
                        signUpVM.onFirstNameTextChanged(text: text)
                    }
                    .focused($focusedField, equals: .name)
                    .textContentType(.givenName)
                    .submitLabel(.next)
                    .onSubmit {
                        focusedField = .lastName
                    }
                    
                    StateTextField(state: uiState.lastName.impl()) { text in
                        signUpVM.onLastNameTextChanged(text: text)
                    }
                    .focused($focusedField, equals: .lastName)
                    .textContentType(.familyName)
                    .submitLabel(.next)
                    .onSubmit {
                        focusedField = .email
                    }
                    
                    StateTextField(state: uiState.email.impl()) { text in
                        signUpVM.onEmailTextChanged(text: text)
                    }
                    .autocapitalization(.none)
                    .focused($focusedField, equals: .email)
                    .textContentType(.emailAddress)
                    .keyboardType(.emailAddress)
                    .submitLabel(.done)
                    
                    Spacer()
                    
                    PasskeyCard(
                        learnMoreState: uiState.learnMoreButton.impl(),
                        cardInfo: uiState.passkeyInfoCardData
                    ) {
                        signUpVM.onLearnMoreClick()
                    }
                }
            }
            StateButton(state: uiState.confirmButton.impl()) {
                signUpVM.onConfirmButtonClick()
            }
        }
        .padding()
        .navigationTitle(uiState.appBar.title.localized())
        .navigationBarTitleDisplayMode(.large)
        .sendLifecycleEventsTo(viewModel: signUpVM)
        .handleErrors(state: uiState.error)
        .handleNavigation(state: uiState.navigation) { navigation in
            switch navigation {
            case is SignUpNavigationDestination.Back:
                navigationStack.path.removeLast()
                break
            case is SignUpNavigationDestination.ToMain:
                navigationStack.path.append(SignInDestination())
                break
            default: break
            }
        }
        .onAppear {
            focusedField = .name
        }
    }
}

#Preview {
    SignUpScreen()
}
