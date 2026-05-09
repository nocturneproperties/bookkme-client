//
//  TextFieldStateImpl.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 26.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//
import shared
import SwiftUI

@Observable
@MainActor
class IOSTextFieldState: IOSViewState, @MainActor TextFieldState, NativeStateRepresentation {
	
	typealias SwiftType = IOSTextFieldState
	
	typealias KotlinType = TextFieldState

    var enabled: Bool
    var supportingTextRes: (any StringDesc)?
	var label: any StringDesc
    var validationState: ValidationState
    var isValid: Bool
    var maxLength: Int32
    var readOnly: Bool
    var text: String
	var startIcon: shared.ImageResource?
	var endIcon: shared.ImageResource?
	var inputType: InputType
	var onTextChanged: ((String) -> Void)?
	var placeholder: any StringDesc
    
	init(
		enabled: Bool = true,
		supportingTextRes: (any StringDesc)? = nil,
		placeholder: any StringDesc = RawStringDesc(string: ""),
		label: any StringDesc = RawStringDesc(string: ""),
		validationState: ValidationState = ValidationState.default_,
		isValid: Bool = true,
		maxLength: Int32 = Int32.max,
		readOnly: Bool = false,
		text: String = "",
		startIcon: shared.ImageResource? = nil,
		endIcon: shared.ImageResource? = nil,
		inputType: InputType = InputType.text,
		onTextChanged: ((String) -> Void)? = nil
	) {
		self.enabled = enabled
		self.supportingTextRes = supportingTextRes
		self.label = label
		self.validationState = validationState
		self.isValid = isValid
		self.maxLength = maxLength
		self.readOnly = readOnly
		self.text = text
		self.startIcon = startIcon
		self.endIcon = endIcon
		self.inputType = inputType
		self.onTextChanged = onTextChanged
		self.placeholder = placeholder
	}
	
	func updateText(desc: (any StringDesc)?) {
		text = desc?.localized() ?? ""
	}
    
}

extension shared.TextFieldState {
    func impl() -> IOSTextFieldState {
        return self as! IOSTextFieldState
    }
	
	func binding() -> Binding<String> {
		return Binding(
			get: {
				return self.text
			},
			set: {
				self.onTextChanged?($0)
			}
		)
	}
}
