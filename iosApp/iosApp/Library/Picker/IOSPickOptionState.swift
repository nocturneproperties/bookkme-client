//
//  IOSPickOptionState.swift
//  iosApp
//
//  Created by BookkMe on 27.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//
import shared

@MainActor
class IOSPickOptionState: @MainActor PickOptionState, NativeStateRepresentation {
	
	typealias SwiftType = IOSPickOptionState
	typealias KotlinType = PickOptionState
	
	var appBar: any AppBarState
	
	var filteredOptions: any ListState
	
	var navigation: any NavigationState
	
	var queryField: any TextFieldState
	
	var selectButton: any ButtonState
	
	init() {
		appBar = IOSAppBarState()
		filteredOptions = IOSListState<IOSPickOptionItem>()
		navigation = IOSNavigationState()
		queryField = IOSTextFieldState()
		selectButton = IOSButtonState()
	}
}
