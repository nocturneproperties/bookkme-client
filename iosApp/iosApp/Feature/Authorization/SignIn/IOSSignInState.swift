//
//  IOSSignInState.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 18.01.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared

class IOSSignInState: SignInState {
    var appBar: any AppBarState
    
    var learnMoreButton: any ButtonState
    
    var passkeyCard: TroubleshootPasskeyCardData
    
    var signInButton: any ButtonState
    
    init(initData: SignInStateInitData) {
        appBar = IOSAppBarState(
            title: initData.title
        )
        learnMoreButton = IOSButtonState(
            text: initData.learnMoreText
        )
        passkeyCard = initData.passkeyCardData
        signInButton = IOSButtonState(
            text: initData.buttonText
        )
    }
}
