//
//  SignInScreen.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 19.01.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//
import shared
import SwiftUI

struct SignInScreen: View {
    
    @EnvironmentObject var navigationStack: NavigationStackHolder
    @StateObject var signInVM: SignInViewModel = AuthDiKt.signInVM()
    
    var body: some View {
        let uiState = signInVM.uiState
        VStack {
            PasskeyCard(
                learnMoreState: uiState.learnMoreButton.impl(),
                cardInfo: uiState.passkeyInfoCardData
            ) {
                signInVM.onLearnMoreClick()
            }
            Spacer()
            StateButton(state: uiState.signInButton.impl()) {
                signInVM.onSignInClick()
            }
            TextButton(state: uiState.signUpButton.impl()) {
                signInVM.onSignUpClick()
            }
            TextButton(state: uiState.troubleshootButton.impl()) {
                signInVM.onTroubleshootClick()
            }
        }
        .padding()
        .navigationTitle(signInVM.uiState.appBar.title.localized())
        .navigationBarTitleDisplayMode(.large)
        .handleErrors(state: uiState.error)
        .handleNavigation(state: uiState.navigation) { navigation in
            switch navigation {
            case is SignInNavigationDestination.Back:
                navigationStack.path.removeLast()
                break
            case is SignInNavigationDestination.ToMain:
                break
            case is SignInNavigationDestination.ToSignUp:
                navigationStack.path.append(SignUpDestination())
                break
            case is SignInNavigationDestination.ToTroubleshoot:
                navigationStack.path.append(TroubleshootDestination())
                break
            default: break
            }
        }
        .sendLifecycleEventsTo(viewModel: signInVM)
    }
}

struct PasskeyCard: View {
    @ObservedObject
    var learnMoreState: IOSButtonState
    
    @State
    var cardInfo: PasskeyInfoCardData
    
    @State
    var onClick: () -> Void
    
    var body: some View {
        VStack(alignment: .leading) {
            Text(cardInfo.title.localized())
                .frame(maxWidth: .infinity, alignment: .leading)
                .font(.headline)
            Text(cardInfo.description_.localized())
                .padding(.top, 8)
                .frame(maxWidth: .infinity, alignment: .leading)
                .font(.caption)
                .foregroundStyle(AppColors.secondary)
            
            TextButton(state: learnMoreState, maxWidth: nil) {
                onClick()
            }
        }
        .padding(24)
        .background(AppColors.elevated)
        .cornerRadius(10)
    }
}

#Preview {
    SignInScreen()
}


