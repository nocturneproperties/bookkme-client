//
//  Atomic.swift
//  iosApp
//
//  Created by BookkMe on 19.03.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import Foundation

@propertyWrapper
struct Atomic<Value> {
	
	private let lock = NSLock()
	private var value: Value
	
	init(default: Value) {
		self.value = `default`
	}
	
	var wrappedValue: Value {
		get {
			lock.lock()
			defer { lock.unlock() }
			return value
		}
		set {
			lock.lock()
			defer { lock.unlock() }
			value = newValue
		}
	}
}
