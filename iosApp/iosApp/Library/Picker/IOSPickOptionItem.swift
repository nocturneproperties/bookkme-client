//
//  IOSPickOptionItem.swift
//  iosApp
//
//  Created by BookkMe on 27.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//
import shared

@MainActor
class IOSPickOptionItem: @MainActor IOSViewState, @MainActor PickOptionItem {
	
	var checkBox: any BooleanState
	var icon: (any ImageDesc)?
	var identity: KeyValueData
	
	init() {
		checkBox = IOSBooleanState()
		icon = nil
		identity = KeyValueData(key: "", value: "")
		super.init(id: identity.key, isVisible: true)
	}
}
