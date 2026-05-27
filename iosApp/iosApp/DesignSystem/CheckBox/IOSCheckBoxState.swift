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
class IOSCheckBoxState: @MainActor IOSViewState, @MainActor CheckBoxState {
	var isChecked: Bool
	
	var isEnabled: Bool
	
	var isValid: Bool
	
	var onCheckedChange: ((KotlinBoolean) -> Void)?
	
	var supportingTextRes: (any StringDesc)?
	
	var text: any StringDesc
	
	var validationState: ValidationState
	
	init() {
		isChecked = false
		isEnabled = true
		isValid = true
		onCheckedChange = nil
		supportingTextRes = nil
		text = RawStringDesc(string: "")
		validationState = ValidationState.default_
		super.init()
	}
}
