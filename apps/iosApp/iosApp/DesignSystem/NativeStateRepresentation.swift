//
//  NativeStateRepresentation.swift
//  iosApp
//
//  Created by BookkMe on 07.09.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import Foundation

protocol NativeStateRepresentation {
	associatedtype SwiftType
	associatedtype KotlinType
}

extension NativeStateRepresentation where Self == SwiftType {
	static func cast(_ kotlinState: KotlinType) -> SwiftType {
		return kotlinState as! SwiftType
	}
}
