//
//  IOSBusinessPluginState.swift
//  iosApp
//
//  Created by BookkMe on 10.06.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//
import shared
import SwiftUI

@Observable
@MainActor
class IOSBusinessPluginState: @MainActor BusinessPluginState, NativeStateRepresentation {
	
	typealias SwiftType = IOSBusinessPluginState
	
	typealias KotlinType = BusinessPluginState
	
	var title: any StringDesc
	var subtitle: any StringDesc
	
	var isEnabled: Bool
	var isExpanded: Bool
	
	let youCan: any ListState
	let clientCan: any ListState
	
	var demo: Action?
	
	let enable: any ButtonState
	
	init() {
		title = RawStringDesc(string: "")
		subtitle = RawStringDesc(string: "")
		isEnabled = false
		isExpanded = false
		youCan = IOSListState<OptionalInfoLine>()
		clientCan = IOSListState<OptionalInfoLine>()
		demo = nil
		enable = IOSButtonState()
	}
}
