//
//  IOSDashboardState.swift
//  iosApp
//
//  Created by BookkMe on 05.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared
import SwiftUI

class IOSDashboardState: DashboardState {
	var navigation: any NavigationState
	
	var tabItems: any TabItemsState
	
	init(initData:TabItemsStateInitData) {
		navigation = IOSNavigationState()
		tabItems = IOSTabItemsState(initData: initData)
	}
}

class IOSTabItemsState: TabItemsState, ObservableObject {
	@Published
	var items: [any TabItem]
	@Published
	var selectedItemId: TabItemId
	
	init(initData:TabItemsStateInitData) {
		selectedItemId = initData.selectedItemId
		items = initData.tabInitData.map { initData in
			IOSTabItem(initData: initData)
		}
	}
}

class IOSTabItem: TabItem, ObservableObject {
	
	var id: TabItemId
	@Published
	var text: any StringDesc
	@Published
	var badgeText: (any StringDesc)?
	
	init(initData:TabItemInitData) {
		badgeText = initData.badgeText
		id = initData.id
		text = initData.text
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
