//
//  IOSCheckBoxState.swift
//  iosApp
//
//  Created by BookkMe on 27.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//
import shared
import SwiftUI

@MainActor
@Observable
class IOSBooleanState: @MainActor IOSViewState, @MainActor BooleanState, NativeStateRepresentation {
	
	typealias SwiftType = IOSBooleanState
	typealias KotlinType = BooleanState
	
	var isChecked: Bool
	var isEnabled: Bool
	var isValid: Bool
	var onCheckedChange: ((KotlinBoolean) -> Void)?
	var supportingTextRes: (any StringDesc)?
	var text: any StringDesc
	var validationState: ValidationState
	
	init(text: any StringDesc = RawStringDesc(string: ""), isChecked: Bool = false) {
		self.isChecked = isChecked
		isEnabled = true
		isValid = true
		onCheckedChange = nil
		supportingTextRes = nil
		self.text = text
		validationState = ValidationState.default_
		super.init()
	}
}
