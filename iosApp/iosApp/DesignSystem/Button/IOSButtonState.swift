//
//  IOSButtonState.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 26.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//
import shared

class IOSButtonState: ButtonState, ObservableObject {
    @Published
    var isEnabled: Bool
    @Published
    var isLoading: Bool
    @Published
    var text: any StringDesc
    
    init(isEnabled: Bool = true, isLoading: Bool = false, text: any StringDesc) {
        self.isEnabled = isEnabled
        self.isLoading = isLoading
        self.text = text
    }
}

extension shared.ButtonState {
    func impl() -> IOSButtonState {
        return self as! IOSButtonState
    }
}
