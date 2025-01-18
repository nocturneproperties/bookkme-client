//
//  IOSAppBarState.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 26.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//

import shared

class IOSAppBarState: shared.AppBarState, ObservableObject {
    var subtitle: (any StringDesc)?
    
    var title: any StringDesc
    
    init(subtitle: (any StringDesc)? = nil, title: any StringDesc) {
        self.subtitle = subtitle
        self.title = title
    }
}
