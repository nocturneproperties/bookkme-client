//
//  IOSBusinessStateFactory.swift
//  iosApp
//
//  Created by BookkMe on 08.05.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared

@MainActor
class IOSBusinessStateFactory: @MainActor BusinessStateFactory {
	
	func createBootstrapState(initData: BusinessBootstrapStateInitData) -> any BusinessBootstrapState {
		return IOSBusinessBootstrapState(initData: initData)
	}
	
	func createBusinessState(initData: CreateBusinessStateInitData) -> any CreateBusinessState {
		return IOSCreateBusinessState(initData: initData)
	}
	
	func createBusinessDashboardState() -> any BusinessDashboardState {
		return IOSBusinessDashboardState()
	}
	
	func createBusinessSettingsState(initData: BusinessSettingsStateInitData) -> any BusinessSettingsState {
		return IOSBusinessSettingsState(initData: initData)
	}
	
	func createBusinessPluginListState() -> any BusinessPluginListState {
		return IOSBusinessPluginListState()
	}
	
	func createBusinessPluginState() -> any BusinessPluginState {
		return IOSBusinessPluginState()
	}
}
