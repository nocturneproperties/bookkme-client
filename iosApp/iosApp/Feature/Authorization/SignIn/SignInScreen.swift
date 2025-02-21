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
	@StateObject var signInVM: SignInViewModel = IOSAuthDiKt.signInVM()
    
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
				navigationStack.path.append(AuthDestination.SignUp())
			}
            TextButton(state: uiState.troubleshootButton.impl()) {
				navigationStack.path.append(AuthDestination.Troubleshoot())
            }
        }
        .padding()
        .navigationTitle(signInVM.uiState.appBar.title.localized())
        .navigationBarTitleDisplayMode(.large)
		.handleNotifications(state: uiState.notification)
        .handleNavigation(state: uiState.navigation) { navigation in
            switch navigation {
            case is SignInNavigationDestination.Main:
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


