//
//  IOSViewState.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 19.01.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared
import SwiftUI

@MainActor
@Observable
class IOSViewState: @MainActor ViewState, @MainActor Identifiable {
	var id: String
	var isVisible: Bool
	
	init(id: String = UUID().uuidString, isVisible: Bool = true) {
		self.isVisible = isVisible
		self.id = id
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
