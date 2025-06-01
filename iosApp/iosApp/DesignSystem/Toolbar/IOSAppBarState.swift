//
//  IOSAppBarState.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 26.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//

import shared

class IOSAppBarState: IOSViewState, AppBarState {
	@Published
    var subtitle: (any StringDesc)?
    
	@Published
    var title: any StringDesc
    
    init(title: any StringDesc, subtitle: (any StringDesc)? = nil, isVisible: Bool = true) {
        self.subtitle = subtitle
        self.title = title
        super.init(isVisible: isVisible)
    }
}

extension shared.AppBarState {
	func impl() -> IOSAppBarState {
		return self as! IOSAppBarState
	}
}
