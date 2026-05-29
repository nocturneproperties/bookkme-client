//
//  IOSAddServiceState.swift
//  iosApp
//
//  Created by BookkMe on 27.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//
import shared
import SwiftUI

@MainActor
@Observable
class IOSAddServiceState: @MainActor IOSViewState, @MainActor AddServiceState, NativeStateRepresentation {
	
	typealias SwiftType = IOSAddServiceState
	typealias KotlinType = AddServiceState
	
	var appBar: any AppBarState
	
	var group: any PickerFieldState
	var name: any TextFieldState
	var price: any TextFieldState
	var duration: any TextFieldState
	var enabled_: any CheckBoxState
	var create: any ButtonState
	
	var navigation: any NavigationState
	var notifications: any PresentationNotificationState
	
	init() {
		appBar = IOSAppBarState()
		group = IOSPickerState()
		name = IOSTextFieldState()
		price = IOSTextFieldState()
		duration = IOSTextFieldState()
		enabled_ = IOSCheckBoxState()
		create = IOSButtonState()
		navigation = IOSNavigationState()
		notifications = IOSNotificationState()
		super.init()
	}
}
