//
//  IOSBusinessDashboardState.swift
//  iosApp
//
//  Created by BookkMe on 01.06.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared
import SwiftUI

class IOSBusinessDashboardState: BusinessDashboardState, ObservableObject {
	
	var appBar: any AppBarState
	
	var navigation: any NavigationState
	
	var notifications: any PresentationNotificationState
	
	@Published
	var sections: [BusinessDashboardSection] = []
	
	init(initData: BusinessDashboardStateInitData) {
		self.appBar = IOSAppBarState(title: RawStringDesc(string: ""))
		self.navigation =  IOSNavigationState()
		self.notifications = IOSNotificationState()
		self.sections = []
	}
	
	func updateSections(sections: [BusinessDashboardSection]) {
		self.sections = sections
	}
}

extension BusinessDashboardState {
	func impl() -> IOSBusinessDashboardState {
		return self as! IOSBusinessDashboardState
	}
}
