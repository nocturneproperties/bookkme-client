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
            TroubleshootCard(
                learnMoreState: uiState.learnMoreButton.impl(),
                troubleshootView: uiState.troubleshootView.impl(),
                troubleshootInfo: uiState.troubleshootCardStaticData
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
        }
        .padding()
        .navigationTitle(signInVM.uiState.appBar.title.localized())
        .navigationBarTitleDisplayMode(.large)
        .handleErrors(state: uiState.error)
        .handleNavigation(state: uiState.navigation) { navigation in
            switch navigation {
            case is SignInNavigationDestinationBack:
                navigationStack.path.removeLast()
                break
            case is SignInNavigationDestinationToMain:
                break
            case is SignInNavigationDestinationToSignUp:
                navigationStack.path.append(SignUpDestination())
                break
            default: break
            }
        }
        .onDisappear {
            signInVM.clear()
        }
    }
    
}

struct TroubleshootCard: View {
    @ObservedObject
    var learnMoreState: IOSButtonState
    
    @ObservedObject
    var troubleshootView: IOSViewState
    
    let troubleshootInfo:TroubleshootCardData
    
    @State
    var onClick: () -> Void
    
    var body: some View {
        if (troubleshootView.isVisible) {
            VStack {
                HStack {
                    Text(troubleshootInfo.title.localized())
                        .font(.body)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .fixedSize(horizontal: false, vertical: true)
                    Image(resource: \.passkey)
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 48, height: 48)
                }
                Divider()
                    .background(AppColors.divider)
                    .padding(.vertical, 8)
                
                ForEach(troubleshootInfo.reasons, id: \.id) { reason in
                    TroubleshootReason(reason: reason)
                }
                
                TextButton(state: learnMoreState) {
                    onClick()
                }
            }
            .padding(24)
            .background(AppColors.elevated)
            .cornerRadius(10)
        }
    }
}

struct TroubleshootReason: View {
    
    let reason: TroubleshootCardData.Reason
    @State
    var isExpanded: Bool = false
    
    var body: some View {
        VStack {
            HStack {
                Text(reason.title.localized())
                    .font(.subheadline)
                    .fontWeight(.semibold)
                    .frame(maxWidth: .infinity, alignment: .leading)
                let degrees = isExpanded ? 90.0 : 0.0
                Image(systemName: "chevron.forward")
                    .rotationEffect(.degrees(degrees))
            }
            
            if (isExpanded) {
                Text(reason.description_.localized())
                    .font(.caption)
                    .foregroundStyle(AppColors.secondary)
                    .padding(.top, 8)
                    .padding(.trailing, 8)
            }
            
        }
        .contentShape(Rectangle())
        .onTapGesture {
            withAnimation {
                isExpanded.toggle()
            }
        }
        .padding(.vertical, 8)
    }
}

#Preview {
    SignInScreen()
}


