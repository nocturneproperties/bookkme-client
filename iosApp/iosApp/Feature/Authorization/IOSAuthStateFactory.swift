//
//  IOSAuthStateFactory.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 26.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//
import shared

class IOSAuthStateFactory:AuthStateFactory {
    func createSignInState(initData: SignInStateInitData) -> any SignInState {
        return IOSSignInState(initData: initData)
    }
    
    func createSignUpState(initData: SignUpStateInitData) -> any SignUpState {
        return IOSSignUpState(initData: initData)
    }
    
}
