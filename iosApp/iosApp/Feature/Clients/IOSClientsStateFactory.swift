//
//  IOSClientsStateFactory.swift
//  iosApp
//
//  Created by BookkMe on 03.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//
import shared

@MainActor
class IOSClientsStateFactory: @MainActor ClientsStateFactory {
	func createClientsListState() -> any ClientsListState {
		return IOSClientsListState()
	}
	
	func createClientState() -> any CreateClientState {
		return IOSCreateClientState()
	}
}
