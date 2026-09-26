//
//  IOSListState.swift
//  iosApp
//
//  Created by Mykhailo Antiufieiev on 03.03.2026.
//  Copyright © 2026 ValthSolutions. All rights reserved.
//

import shared
import Observation
import SwiftUI

@Observable
@MainActor
class IOSListState<T>: @MainActor IOSViewState, @MainActor ListState, NativeStateRepresentation {
	typealias SwiftType = IOSListState<T>
	
	typealias KotlinType = ListState
	
    
    var typedItems: [T] = []
    var items: [Any] { typedItems }
    var emptyState: EmptyState? = nil
    var errorState: ErrorState? = nil
    var bannerError: BannerErrorState? = nil
    var loadMore: (() -> Void)? = nil
    var isInitialLoading: Bool = true
    
    init(typedItems: [T] = []) {
        self.typedItems = typedItems
        self.isInitialLoading = typedItems.isEmpty
		super.init()
    }
    
    func append(list: [Any]) {
		withAnimation {
			typedItems.append(contentsOf: list as! [T])
			isInitialLoading = false
			demoteErrorToBannerIfHasItems()
		}
    }
    
    func clear() {
		withAnimation {
			typedItems.removeAll()
			isInitialLoading = false
		}
    }
    
    func replace(list: [Any]) {
		withAnimation {
			typedItems = list as! [T]
			isInitialLoading = false
			demoteErrorToBannerIfHasItems()
		}
    }

	private func demoteErrorToBannerIfHasItems() {
		guard let error = errorState, !typedItems.isEmpty else { return }
		bannerError = BannerErrorState.companion.default(onRetryClick: error.onRetryClick)
		errorState = nil
	}
}

@MainActor
extension ListState {
    func items<T>(_ type: T.Type) -> [T] where T: AnyObject {
        return (self as! IOSListState<T>).typedItems
    }
}
