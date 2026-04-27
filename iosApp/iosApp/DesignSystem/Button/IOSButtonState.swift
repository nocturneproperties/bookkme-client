//
//  IOSButtonState.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 26.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//
import shared

@MainActor
@Observable
class IOSButtonState: IOSViewState, @MainActor ButtonState {

    var isEnabled: Bool
    var isLoading: Bool
    var text: any StringDesc
	var icon: ImageResource?
	var onClick: (() -> Void)?
    
	init(
		text: any StringDesc,
		isEnabled: Bool = true,
		isLoading: Bool = false,
		icon: ImageResource? = nil,
		onClick: (() -> Void)? = nil,
		isVisible: Bool = true
	) {
        self.isEnabled = isEnabled
        self.isLoading = isLoading
        self.text = text
		self.icon = icon
		self.onClick = onClick
        super.init(isVisible: isVisible)
    }
}

extension shared.ButtonState {
    func impl() -> IOSButtonState {
        return self as! IOSButtonState
    }
}
