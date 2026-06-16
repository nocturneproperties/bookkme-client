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
	@EnvironmentObject var navigationStack: NavigationStackHolder
    @StateViewModel var signUpVM: SignUpViewModel = IOSAuthDiKt.signUpVM()
    
    var body: some View {
        let uiState = signUpVM.uiState
		ScrollView {
			SignUpScreenContent(signUpVM: signUpVM)
				.padding()
				.withNavigationBar(uiState.appBar)
				.sendLifecycleEventsTo(signUpVM)
				.handleNotifications(uiState.notification)
				.handleNavigation(uiState.navigation) { dest in
					switch dest {
					case is SignUpNavigationDestination.Back:
						navigationStack.popLast()
						
					default:
						break
					}
				}
		}.background(AppColors.background)
    }
}

struct SignUpScreenContent: View {
	private enum FocusField {
		case name
		case lastName
		case email
	}
	@FocusState private var focusedField: FocusField?
	var signUpVM: SignUpViewModel
	
	var body: some View {
		let uiState = signUpVM.uiState
		VStack {
			StateTextField(uiState.name.impl()) { text in
				signUpVM.onFirstNameTextChanged(text: text)
			}
			.focused($focusedField, equals: .name)
			.textContentType(.givenName)
			.submitLabel(.next)
			.onSubmit {
				focusedField = .lastName
			}
			
			StateTextField(uiState.lastName.impl()) { text in
				signUpVM.onLastNameTextChanged(text: text)
			}
			.focused($focusedField, equals: .lastName)
			.textContentType(.familyName)
			.submitLabel(.next)
			.onSubmit {
				focusedField = .email
			}
			
			StateTextField(uiState.email.impl()) { text in
				signUpVM.onEmailTextChanged(text: text)
			}
			.autocapitalization(.none)
			.focused($focusedField, equals: .email)
			.textContentType(.emailAddress)
			.keyboardType(.emailAddress)
			.submitLabel(.done)
			
			PasskeyCard(
				learnMoreState: uiState.learnMoreButton.impl(),
				cardInfo: uiState.passkeyInfoCardData
			) {
				signUpVM.onLearnMoreClick()
			}
			Spacer(minLength: 20)
			StateButton(uiState.confirmButton.impl()) {
				signUpVM.onConfirmButtonClick()
			}
		}
	}
}

#Preview {
    SignUpScreen()
}
