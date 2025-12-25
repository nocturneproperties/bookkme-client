//
//  IOSViewState.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 19.01.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared
import SwiftUI

class IOSViewState: ViewState, ObservableObject {
    
    @Published
    var isVisible: Bool
    
    init(isVisible: Bool = true) {
        self.isVisible = isVisible
    }
}

extension shared.ViewState {
    func impl() -> IOSViewState {
        return self as! IOSViewState
    }
}

extension View {
	@ViewBuilder
	func `if`<Content: View>(_ condition: Bool, transformTrue: (Self) -> Content, transformFalse: (Self) -> Content) -> some View {
		if condition {
			transformTrue(self)
		} else {
			transformFalse(self)
		}
	}
	
	@ViewBuilder
	func `if`<Content: View>(_ condition: Bool, transform: (Self) -> Content) -> some View {
		if condition {
			transform(self)
		} else {
			self
		}
	}
}
