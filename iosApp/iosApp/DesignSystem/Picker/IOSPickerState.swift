//
//  IOSPickerState.swift
//  iosApp
//
//  Created by BookkMe on 06.09.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared
import SwiftUI

class IOSPickerState<T:PickerPresentation>: PickerFieldState, ObservableObject {
	
	@Published
	var options: [PickerPresentation]
	@Published
	var selectedItem: PickerPresentation
	@Published
	var text: StringDesc
	
	init(options: [PickerPresentation], selectedItem: PickerPresentation, text: StringDesc = RawStringDesc(string: "")) {
		self.options = options
		self.selectedItem = selectedItem
		self.text = text
	}
}

extension PickerFieldState {
	func impl<T:PickerPresentation>() -> IOSPickerState<T> {
		return self as! IOSPickerState<T>
	}
}

