//
//  IOSPickerState.swift
//  iosApp
//
//  Created by BookkMe on 06.09.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared
import SwiftUI

@Observable
@MainActor
class IOSPickerState: IOSViewState, @MainActor PickerFieldState, NativeStateRepresentation {
	
	typealias SwiftType = IOSPickerState
	
	typealias KotlinType = PickerFieldState
	
	var onItemPicked: (PickerPresentation?) -> Void
	var options: [PickerPresentation]
	var pickerTitle: any StringDesc
	var pickerType: PickerFieldStatePickerType = PickerFieldStatePickerType.bottomSheet
	var selectedItem: PickerPresentation? = nil
	var textField: any TextFieldState
	
	init(
		pickerTitle: any StringDesc = RawStringDesc(string: ""),
		options: [PickerPresentation] = [],
		pickerType: PickerFieldStatePickerType = PickerFieldStatePickerType.bottomSheet,
		textField: (any TextFieldState)? = nil,
		onItemPicked: @escaping (PickerPresentation?) -> Void = {_ in },
	) {
		self.onItemPicked = onItemPicked
		self.options = options
		self.pickerTitle = pickerTitle
		self.pickerType = pickerType
		self.textField = textField ?? IOSTextFieldState(readOnly: true)
	}
	
	
	func replaceOptions(options_: [PickerPresentation]) {
		self.options = options
	}
}

extension PickerFieldState {
	func impl() -> IOSPickerState {
		return self as! IOSPickerState
	}
}

