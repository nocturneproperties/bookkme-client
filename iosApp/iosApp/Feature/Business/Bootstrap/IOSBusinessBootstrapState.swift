//
//  IOSBootstrapState.swift
//  iosApp
//
//  Created by BookkMe on 08.05.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared

@MainActor
@Observable
class IOSBusinessBootstrapState: @MainActor BusinessBootstrapState {
	
	var startDestination: BusinessDestination
	
	var notification: any PresentationNotificationState
	
	init(initData:BusinessBootstrapStateInitData) {
		startDestination = initData.initialDestination
		notification = IOSNotificationState()
	}

}

extension BusinessBootstrapState {
	func impl() -> IOSBusinessBootstrapState {
		return self as! IOSBusinessBootstrapState
	}
}
