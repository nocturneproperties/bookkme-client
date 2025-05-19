//
//  TextFieldStateImpl.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 26.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//
import shared

class IOSTextFieldState: IOSViewState, TextFieldState {
    
    @Published
    var enabled: Bool
    @Published
    var supportingTextRes: (any StringDesc)?
    @Published
    var hint: any StringDesc
	@Published
	var label: any StringDesc
    @Published
    var isError: Bool
    @Published
    var isValid: Bool
    @Published
    var maxLength: Int32
    @Published
    var readOnly: Bool
    @Published
    var text: String
    
    init(
        enabled: Bool = true,
        supportingTextRes: (any StringDesc)? = nil,
        hint: any StringDesc = RawStringDesc(string: ""),
		label: any StringDesc = RawStringDesc(string: ""),
        isError: Bool = false,
        isValid: Bool = false,
        maxLength: Int32 = Int32.max,
        readOnly: Bool = false,
        text: String = "",
        isVisible: Bool = true
    ) {
        self.enabled = enabled
        self.supportingTextRes = supportingTextRes
        self.hint = hint
        self.isError = isError
        self.isValid = isValid
        self.maxLength = maxLength
        self.readOnly = readOnly
        self.text = text
		self.label = label
        super.init(isVisible: isVisible)
    }
    
}

extension shared.TextFieldState {
    func impl() -> IOSTextFieldState {
        return self as! IOSTextFieldState
    }
}
