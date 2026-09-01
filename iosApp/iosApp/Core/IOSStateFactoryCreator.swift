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
	
	func createAppointmentsFactory() -> any AppointmentsStateFactory {
		return IOSAppointmentsStateFactory()
	}

    func createAuthFactory() -> any AuthStateFactory {
        return IOSAuthStateFactory()
    }
	
	func createBusinessFactory() -> any BusinessStateFactory {
		return IOSBusinessStateFactory()
	}
    
	func createClientsFactory() -> any ClientsStateFactory {
		return IOSClientsStateFactory()
	}

	func createCredentialModuleFactory() -> any CredentialModuleFactory {
		return IOSCredentialFactory()
	}
	
	func createDashboardFactory() -> any DashboardStateFactory {
		return IOSDashboardStateFactory()
	}

	func createEmployeesFactory() -> any EmployeesStateFactory {
		return IOSEmployeesStateFactory()
	}

	func createPickOptionFactory() -> any PickOptionStateFactory {
		return IOSPickOptionFactory()
	}

	func createServicesFactory() -> any ServicesStateFactory {
		return IOSServicesStateFactory()
	}

	func createSettingsFactory() -> any SettingsStateFactory {
		return IOSSettingsStateFactory()
	}
}
