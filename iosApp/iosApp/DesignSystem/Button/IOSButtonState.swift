//
//  IOSButtonState.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 26.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//
import shared

class IOSButtonState: IOSViewState, ButtonState {
    @Published
    var isEnabled: Bool
    @Published
    var isLoading: Bool
    @Published
    var text: any StringDesc
    
    init(text: any StringDesc, isEnabled: Bool = true, isLoading: Bool = false, isVisible: Bool = true) {
        self.isEnabled = isEnabled
        self.isLoading = isLoading
        self.text = text
        super.init(isVisible: isVisible)
    }
}

extension shared.ButtonState {
    func impl() -> IOSButtonState {
        return self as! IOSButtonState
    }
}
