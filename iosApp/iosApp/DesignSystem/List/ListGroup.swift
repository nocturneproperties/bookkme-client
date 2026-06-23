//
//  ListGroup.swift
//  iosApp
//
//  Created by BookkMe on 03.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct ListGroup<T, S: ListStyle,Header: View, Content: View>:View where T:AnyObject, T:Identifiable {
	
	@Bindable
	var listState: IOSListState<T>
	
	let listStyle: S
	
	@ViewBuilder
	let content: (T) -> Content
	
	@ViewBuilder
	let header: () -> Header
	
	init(listState: IOSListState<T>, listStyle: S, content: @escaping (T) -> Content, header: @escaping () -> Header) {
		self._listState = Bindable(wrappedValue: listState)
		self.content = content
		self.header = header
		self.listStyle = listStyle
	}
	
	init(listState: IOSListState<T>, content: @escaping (T) -> Content) where Header == EmptyView, S == PlainListStyle {
		self.init(listState: listState, listStyle: .plain, content: content) { EmptyView() }
	}
	
	init(listState: IOSListState<T>, listStyle: S, content: @escaping (T) -> Content) where Header == EmptyView {
		self.init(listState: listState, listStyle: listStyle, content: content) { EmptyView() }
	}
	
	var body: some View {
		Group {
			if (!listState.isInitialLoading) {
				List {
					header()
					ForEach(listState.typedItems) { item in
						content(item)
					}
				}
				.listStyle(listStyle)
			} else {
				ProgressView()
			}
		}.overlay {
			if let emptyState = listState.emptyState, !listState.isInitialLoading, listState.items.isEmpty {
				ListEmptyView(state: emptyState)
			}
		}
	}
}

