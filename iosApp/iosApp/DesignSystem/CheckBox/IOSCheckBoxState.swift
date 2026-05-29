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
class IOSCheckBoxState: @MainActor IOSViewState, @MainActor CheckBoxState, NativeStateRepresentation {
	
	typealias SwiftType = IOSCheckBoxState
	typealias KotlinType = CheckBoxState
	
	var isChecked: Bool
	
	var isEnabled: Bool
	
	var isValid: Bool
	
	var onCheckedChange: ((KotlinBoolean) -> Void)?
	
	var supportingTextRes: (any StringDesc)?
	
	var text: any StringDesc
	
	var validationState: ValidationState
	
	init(text: any StringDesc = RawStringDesc(string: "")) {
		isChecked = false
		isEnabled = true
		isValid = true
		onCheckedChange = nil
		supportingTextRes = nil
		self.text = text
		validationState = ValidationState.default_
		super.init()
	}
}
