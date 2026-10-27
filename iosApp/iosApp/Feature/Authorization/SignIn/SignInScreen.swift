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
	@StateViewModel var signInVM: SignInViewModel = IOSAuthDiKt.signInVM()
	
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
            StateButton(uiState.signInButton.impl()) {
                signInVM.onSignInClick()
            }
			TextButton(uiState.signUpButton.impl()) {
				navigationStack.path.append(AuthDestination.SignUp())
			}
			.buttonStyle(.textStandalone)
            TextButton(uiState.troubleshootButton.impl()) {
				navigationStack.path.append(AuthDestination.Troubleshoot())
            }
			.buttonStyle(.textStandalone)
        }
        .padding()
		.background(AppColors.background)
		.withNavigationBar(signInVM.uiState.appBar)
		.handleNotifications(uiState.notification)
        .handleNavigation(uiState.navigation) { navigation in
            switch navigation {
            case is SignInNavigationDestination.Main:
                break
            default: break
            }
        }
        .sendLifecycleEventsTo(signInVM)
    }
}

struct PasskeyCard: View {
	
    let learnMoreState: IOSButtonState
    
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
            
            TextButton(learnMoreState) {
                onClick()
            }
			.buttonStyle(.textStandalone)
        }
		.padding(.horizontal, 24)
		.padding(.top, 24)
        .background(AppColors.elevated)
        .cornerRadius(10)
    }
}

#Preview {
    SignInScreen()
}


