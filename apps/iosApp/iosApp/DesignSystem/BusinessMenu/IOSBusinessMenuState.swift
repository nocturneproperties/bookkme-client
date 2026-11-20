//
//  IOSBusinessMenuState.swift
//  iosApp
//
//  Created by BookkMe on 05.09.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import shared

@MainActor
@Observable
class IOSBusinessMenuState: @MainActor BusinessMenuState {
	var items: [BusinessMenuItem] = []
	var selectedBusinessId: KotlinUuid?
	var onBusinessClick: ((KotlinUuid) -> Void)?
	var onCreateClick: (() -> Void)?
	var onJoinClick: (() -> Void)?
}

extension BusinessMenuState {
	func impl() -> IOSBusinessMenuState {
		return self as! IOSBusinessMenuState
	}
}
