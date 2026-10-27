//
//  IOSCredentialFactory.swift
//  iosApp
//

import shared

class IOSCredentialFactory: CredentialModuleFactory{
    func createCredentialManager() -> any CredentialManager {
        return IOSCredentialManager()
    }
}
