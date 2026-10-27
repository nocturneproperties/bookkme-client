//
//  IOSPickOptionFactory.swift
//  iosApp
//
//  Created by BookkMe on 27.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//
import shared

@MainActor
class IOSPickOptionFactory: @MainActor PickOptionStateFactory {
	func createPickOptionItem() -> any PickOptionItem {
		return IOSPickOptionItem()
	}
	
	func createPickOptionState() -> any PickOptionState {
		return IOSPickOptionState()
	}
}
