//
//  IOSSignUpState.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 26.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//

import shared

class IOSSignUpState: SignUpState {
    var appBar: any AppBarState
    
    var email: any TextFieldState
    
    var lastName: any TextFieldState
    
    var name: any TextFieldState
    
    var confirmButton: any ButtonState
    
    init(initData: SignUpStateInitData) {
        appBar = IOSAppBarState(
            title: initData.title
        )
        name = IOSTextFieldState(
            hint: initData.nameHint
        )
        lastName = IOSTextFieldState(
            hint: initData.lastNameHint
        )
        email = IOSTextFieldState(
            hint: initData.emailHint
        )
        confirmButton = IOSButtonState(
            isEnabled: false,
            text: initData.confirmButtonText
        )
    }
}
