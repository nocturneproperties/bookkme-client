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
    
    var signInButton: any ButtonState
    
    var signUpButton: any ButtonState
    
    var passkeyInfoCardData: PasskeyInfoCardData
    
    var troubleshootButton: any ButtonState
    
    var error: any ErrorState
    
    var navigation: any NavigationState
    
    init(initData: SignInStateInitData) {
        appBar = IOSAppBarState(title: initData.title)
        learnMoreButton = IOSButtonState(text: initData.learnMoreText)
        passkeyInfoCardData = initData.passkeyInfoCardData
        troubleshootButton = IOSButtonState(text: initData.troubleshootButtonText)
        signInButton = IOSButtonState(text: initData.signInButtonText)
        signUpButton = IOSButtonState(text: initData.signUpButtonText)
        error = IOSErrorState()
        navigation = IOSNavigationState()
    }
}
