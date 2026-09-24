//
//  IOSDashboardState.swift
//  iosApp
//
//  Created by BookkMe on 05.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared
import SwiftUI

@MainActor
@Observable
class IOSDashboardState: @MainActor DashboardState {
	var navigation: any NavigationState

	var tabItems: any TabItemsState

	var home: any DashboardHomeState

	var notifications: any PresentationNotificationState

	init(initData:TabItemsStateInitData) {
		navigation = IOSNavigationState()
		tabItems = IOSTabItemsState(initData: initData)
		home = IOSDashboardHomeState()
		notifications = IOSNotificationState()
	}
}

@MainActor
@Observable
class IOSDashboardHomeState: @MainActor DashboardHomeState {
	var content: HomeContent?
	var onboarding: any OnboardingState

	init() {
		content = nil
		onboarding = IOSOnboardingState()
	}
}

@MainActor
@Observable
class IOSOnboardingState: @MainActor OnboardingState {
	var isBusinessStepDone: Bool = false
	var isPluginsStepUnlocked: Bool = false
	var onCreateBusinessClick: (() -> Void)?
	var onEnablePluginsClick: (() -> Void)?
}

@MainActor
@Observable
class IOSTabItemsState: @MainActor TabItemsState {
	
	var items: [any TabItem]
	var selectedItemId: TabItemId
	
	init(initData: TabItemsStateInitData) {
		selectedItemId = initData.selectedItemId
		items = initData.tabInitData.map { initData in
			IOSTabItem(initData: initData)
		}
	}
}

@MainActor
@Observable
class IOSTabItem: @MainActor TabItem {
	
	var id: TabItemId
	var text: any StringDesc
	var badgeText: (any StringDesc)?
	var isEnabled: Bool

	init(initData:TabItemInitData) {
		badgeText = initData.badgeText
		id = initData.id
		text = initData.text
		isEnabled = initData.isEnabled
	}
}

extension DashboardState {
	func impl() -> IOSDashboardState {
		return self as! IOSDashboardState
	}
}

extension TabItemsState {
	func impl() -> IOSTabItemsState {
		return self as! IOSTabItemsState
	}
}

extension TabItem {
	func impl() -> IOSTabItem {
		return self as! IOSTabItem
	}
}

extension DashboardHomeState {
	func impl() -> IOSDashboardHomeState {
		return self as! IOSDashboardHomeState
	}
}

extension OnboardingState {
	func impl() -> IOSOnboardingState {
		return self as! IOSOnboardingState
	}
}
