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
        let uiState = IOSSignUpState.cast(signUpVM.uiState)
		List {
			SignUpScreenContent(state: uiState)
		}
		.toolbar {
			TextButton(uiState.confirmButton)
		}
		.listSectionSpacing(.compact)
		.scrollDismissesKeyboard(.immediately)
		.withNavigationBar(uiState.appBar)
		.sendLifecycleEventsTo(signUpVM)
		.handleNotifications(uiState.notification)
		.handleNavigation(uiState.navigation) { dest in
			switch dest {
			case is SignUpNavigationDestination.Back:
				navigationStack.popLast()

			case let verifyEmail as SignUpNavigationDestination.VerifyEmail:
				navigationStack.push(verifyEmail)
				
			default:
				break
			}
		}
    }
}

struct SignUpScreenContent: View {
	private enum FocusField {
		case name
		case lastName
		case email
	}
	@FocusState private var focusedField: FocusField?
	var state: IOSSignUpState
	
	var body: some View {
		let uiState = state
		
		SectionTextField(uiState.name)
		.focused($focusedField, equals: .name)
		.textContentType(.givenName)
		.submitLabel(.next)
		.onSubmit {
			focusedField = .lastName
		}
		.textFieldStyle(.inList)
		
		SectionTextField(uiState.lastName)
		.focused($focusedField, equals: .lastName)
		.textContentType(.familyName)
		.submitLabel(.next)
		.onSubmit {
			focusedField = .email
		}
		.textFieldStyle(.inList)
		
		SectionTextField(uiState.email)
		.autocapitalization(.none)
		.focused($focusedField, equals: .email)
		.textContentType(.emailAddress)
		.keyboardType(.emailAddress)
		.submitLabel(.done)
		.textFieldStyle(.inList)
		
		SectionStateCheckBox(
			uiState.legalAcceptance,
			label: Text(acceptanceLabel()),
			textColor: AppColors.secondary,
			textSize: 10
		)
			.environment(\.openURL, OpenURLAction { url in
				guard let documents = uiState.legalDocuments,
					  let document = [documents.privacyPolicy, documents.termsOfService]
					.first(where: { URL(string: $0.linkUrl) == url }) else {
					return .discarded
				}
				document.onLinkClick?()
				return .handled
			})
		
		if uiState.reloadLegalDocuments.isVisible {
			TextButton(uiState.reloadLegalDocuments)
		}

		Section {
			PasskeyCard(
				learnMoreState: uiState.learnMoreButton.impl(),
				cardInfo: uiState.passkeyInfoCardData
			) {}
			.listRowInsets(EdgeInsets())
		}
	}

    private func acceptanceLabel() -> AttributedString {
        var label = AttributedString(state.legalAcceptance.text.localized())
        guard let documents = state.legalDocuments else {
            return label
        }
        for document in [documents.privacyPolicy, documents.termsOfService] {
            if let range = label.range(of: document.linkText.localized()) {
                label[range].link = URL(string: document.linkUrl)
                label[range].foregroundColor = AppColors.actionText
                label[range].underlineStyle = .single
            }
        }
        return label
    }
}

#Preview {
    SignUpScreen()
}
