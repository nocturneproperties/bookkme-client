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
class IOSListState<T>: @MainActor ListState {
    
    var typedItems: [T] = []
    var items: [Any] { typedItems }
    var emptyState: EmptyState? = nil
    var loadMore: (() -> Void)? = nil
    var isInitialLoading: Bool = true
    
    init(typedItems: [T] = []) {
        self.typedItems = typedItems
        self.isInitialLoading = typedItems.isEmpty
    }
    
    func append(list: [Any]) {
		withAnimation {
			typedItems.append(contentsOf: list as! [T])
			isInitialLoading = false
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
		}
    }
}

@MainActor
extension ListState {
    func items<T>(_ type: T.Type) -> [T] where T: AnyObject {
        return (self as! IOSListState<T>).typedItems
    }
}
