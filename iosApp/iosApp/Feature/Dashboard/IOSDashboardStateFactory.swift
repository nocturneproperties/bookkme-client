//
//  IOSDashboardStateFactory.swift
//  iosApp
//
//  Created by BookkMe on 05.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//
import shared

class IOSDashboardStateFactory: DashboardStateFactory {
	func createDashboardState(initData: TabItemsStateInitData) -> any DashboardState {
		return IOSDashboardState(initData: initData)
	}
}
