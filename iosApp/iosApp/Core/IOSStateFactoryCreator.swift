//
//  IOSStateFactoryCreator.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 26.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//
import shared

@MainActor
class IOSStateFactoryCreator: @MainActor StateFactoryCreator {
	
	func createDashboardFactory() -> any DashboardStateFactory {
		return IOSDashboardStateFactory()
	}
	
    func createAuthFactory() -> any AuthStateFactory {
        return IOSAuthStateFactory()
    }
	
	func createSettingsFactory() -> any SettingsStateFactory {
		return IOSSettingsStateFactory()
	}
	
	func createBusinessFactory() -> any BusinessStateFactory {
		return IOSBusinessStateFactory()
	}
    
	func createCredentialModuleFactory() -> any CredentialModuleFactory {
		return IOSCredentialFactory()
	}
}
