//
//  IOSViewState.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 19.01.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared

class IOSViewState: ViewState, ObservableObject {
    
    @Published
    var isVisible: Bool
    
    init(isVisible: Bool = true) {
        self.isVisible = isVisible
    }
}
