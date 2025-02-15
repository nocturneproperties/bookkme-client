//
//  IOSTextState.swift
//  iosApp
//
//  Created by BookkMe on 07.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

class IOSTextState: IOSViewState, TextState {
	
	var text: any StringDesc
	
	var isHighlighted: Bool
	
	init(text: any StringDesc, isHighlighted: Bool = false, isVisible: Bool = true) {
		self.isHighlighted = isHighlighted
		self.text = text
		super.init(isVisible: isVisible)
	}
}

extension TextState {
	func impl() -> IOSTextState {
		return self as! IOSTextState
	}
}
