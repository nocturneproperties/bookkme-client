//
//  LogOutHandler.swift
//  iosApp
//
//  Created by BookkMe on 04.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//
import Foundation

@Observable
class LogOutHandler: ObservableObject {
	var onLogOut: () -> Void
	
	init(onLogOut: @escaping () -> Void) {
		self.onLogOut = onLogOut
	}
}
