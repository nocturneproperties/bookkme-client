//
//  IOSSwitchState.swift
//  iosApp
//
//  Created by BookkMe on 07.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared

class IOSSwitchState: IOSViewState, SwitchState {
	
	@Published
	var isChecked: Bool
	@Published
	var text: any StringDesc
	
	init(text: any StringDesc, isChecked: Bool) {
		self.isChecked = isChecked
		self.text = text
	}
}

extension SwitchState {
	func impl() -> IOSSwitchState {
		return self as! IOSSwitchState
	}
}
