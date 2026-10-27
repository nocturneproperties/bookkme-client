//
//  IOSRefreshState.swift
//  iosApp
//
//  Created by BookkMe on 29.03.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared

@MainActor
@Observable
class IOSRefreshState: @MainActor RefreshState {
	
	var isRefreshing: Bool = false
	var onRefresh: () -> Void
	
	init(isRefreshing: Bool = false, onRefresh: @escaping () -> Void = {}) {
		self.isRefreshing = isRefreshing
		self.onRefresh = onRefresh
	}
	
	func awaitRefresh() async {
		onRefresh()
		await waitForNextValue()
	}
	
	private func waitForNextValue() async {
		await withCheckedContinuation { continuation in
			withObservationTracking({
				_ = isRefreshing
			}) {
				Task { @MainActor in
					continuation.resume()
				}
			}
		}
		if (isRefreshing) {
			await waitForNextValue()
		}
	}
}

extension RefreshState {
	func impl() -> IOSRefreshState {
		return self as! IOSRefreshState
	}
}
