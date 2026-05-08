//
//  ListGroup.swift
//  iosApp
//
//  Created by BookkMe on 03.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct ListGroup<T, Content: View>:View where T:AnyObject, T:Identifiable {
	
	var listState: IOSListState<T>
	
	@ViewBuilder
	let content: (T) -> Content
	
	var body: some View {
		Group {
			if (!listState.items.isEmpty) {
				List {
					ForEach(listState.typedItems) { item in
						content(item)
							.listRowSeparator(.hidden)
					}
				}
				.listStyle(.plain)
			} else if let emptyState = listState.emptyState, !listState.isInitialLoading {
				EmptyView(state: emptyState)
			} else {
				ProgressView()
			}
		}
	}
}

