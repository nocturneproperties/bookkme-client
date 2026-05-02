//
//  IOSAppBarState.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 26.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//

import shared

@Observable
@MainActor
final class IOSAppBarState: @MainActor IOSViewState, @MainActor AppBarState {
		
	var title: any StringDesc
	var subtitle: (any StringDesc)?
	var actions: any ListState
	var size_: TopBarSize
	var onBackClick: (() -> Void)?
	
	init(
		title: any StringDesc = RawStringDesc(string: ""),
		subtitle: (any StringDesc)? = nil,
		isVisible: Bool = true,
		size: TopBarSize = .large,
	) {
		self.subtitle = subtitle
		self.title = title
		self.actions = IOSListState<AppBarAction>()
		self.size_ = size
		super.init(isVisible: isVisible)
	}
}

extension shared.AppBarState {
	func impl() -> IOSAppBarState {
		return self as! IOSAppBarState
	}
}
