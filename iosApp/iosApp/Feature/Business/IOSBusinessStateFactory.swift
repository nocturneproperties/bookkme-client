//
//  IOSBusinessStateFactory.swift
//  iosApp
//
//  Created by BookkMe on 08.05.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared

class IOSBusinessStateFactory: BusinessStateFactory {
	func createBootstrapState(initData: BusinessBootstrapStateInitData) -> any BusinessBootstrapState {
		return IOSBusinessBootstrapState(initData: initData)
	}
	
	func createBusinessState(initData: CreateBusinessStateInitData) -> any CreateBusinessState {
		return IOSCreateBusinessState(initData: initData)
	}
}
