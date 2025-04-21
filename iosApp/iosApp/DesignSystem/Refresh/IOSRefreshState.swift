//
//  IOSRefreshState.swift
//  iosApp
//
//  Created by BookkMe on 29.03.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared

class IOSRefreshState: RefreshState, ObservableObject {
	
	@Published
	var isRefreshing: Bool = false
	
	func awaitRefresh() async {
		var iterator = $isRefreshing.values.dropFirst().makeAsyncIterator()
		var isLoading = true
		while (isLoading != false) {
			isLoading = await iterator.next() ?? false
		}
	}
}

extension RefreshState {
	func impl() -> IOSRefreshState {
		return self as! IOSRefreshState
	}
}
