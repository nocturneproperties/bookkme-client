//
//  IOSTroubleshootState.swift
//  iosApp
//
//  Created by BookkMe on 04.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared

class IOSTroubleshootState: TroubleshootState {
    var appBar: any AppBarState
    
    var contactSupportButton: any ButtonState
    
    var navigation: any NavigationState
    
    var troubleshootCardStaticData: TroubleshootCardData
    
    init(initData:TroubleshootStateInitData) {
        appBar = IOSAppBarState(title: initData.title)
        contactSupportButton = IOSButtonState(text: initData.buttonText)
        navigation = IOSNavigationState()
        troubleshootCardStaticData = initData.cardData
    }
}
