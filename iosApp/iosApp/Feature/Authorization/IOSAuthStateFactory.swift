//
//  IOSAuthStateFactory.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 26.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//
import shared

@MainActor
class IOSAuthStateFactory: @MainActor AuthStateFactory {
    
    func createBootstrapState() -> any BootstrapState {
        return IOSBootstrapState()
    }
    
    func createSignInState(initData: SignInStateInitData) -> any SignInState {
        return IOSSignInState(initData: initData)
    }
    
    func createSignUpState() -> any SignUpState {
        return IOSSignUpState()
    }

    func createSignUpLegalDocumentState() -> any SignUpLegalDocumentState {
        return IOSSignUpLegalDocumentState()
    }
    
    func createTroubleshootState(initData: TroubleshootStateInitData) -> any TroubleshootState {
        return IOSTroubleshootState(initData: initData)
    }

    func createVerifyEmailState() -> any VerifyEmailState {
        return IOSVerifyEmailState()
    }

    func createChangeEmailState() -> any ChangeEmailState {
        return IOSChangeEmailState()
    }
    
}
