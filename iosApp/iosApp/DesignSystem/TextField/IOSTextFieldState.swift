//
//  TextFieldStateImpl.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 26.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//
import shared

class IOSTextFieldState: shared.TextFieldState, ObservableObject {
    
    @Published
    var enabled: Bool
    @Published
    var errorTextRes: (any StringDesc)?
    @Published
    var hint: any StringDesc
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
        errorTextRes: (any StringDesc)? = nil,
        hint: any StringDesc = RawStringDesc(string: ""),
        isError: Bool = false,
        isValid: Bool = false,
        maxLength: Int32 = Int32.max,
        readOnly: Bool = false,
        text: String = ""
    ) {
        self.enabled = enabled
        self.errorTextRes = errorTextRes
        self.hint = hint
        self.isError = isError
        self.isValid = isValid
        self.maxLength = maxLength
        self.readOnly = readOnly
        self.text = text
    }
    
}

extension shared.TextFieldState {
    func impl() -> IOSTextFieldState {
        return self as! IOSTextFieldState
    }
}
