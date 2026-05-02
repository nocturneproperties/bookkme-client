//
//  IOSSwitchState.swift
//  iosApp
//
//  Created by BookkMe on 07.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared

@MainActor
@Observable
final class IOSSwitchState: IOSViewState, @MainActor SwitchState {
	
	var isChecked: Bool
	var text: any StringDesc
	
	init(text: any StringDesc, isChecked: Bool) {
		self.isChecked = isChecked
		self.text = text
		super.init(isVisible: true)
	}
}

extension SwitchState {
	func impl() -> IOSSwitchState {
		guard let state = self as? IOSSwitchState else {
			preconditionFailure("SwitchState is not IOSSwitchState")
		}
		return state
	}
}

