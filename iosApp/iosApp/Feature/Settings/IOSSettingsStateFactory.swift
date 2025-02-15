//
//  IOSSettingsStateFactory.swift
//  iosApp
//
//  Created by BookkMe on 07.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared

class IOSSettingsStateFactory: SettingsStateFactory {
	func createSettingsState(initData: SettingsStateInitData) -> any SettingsState {
		return IOSSettingsState(initData: initData)
	}
}
