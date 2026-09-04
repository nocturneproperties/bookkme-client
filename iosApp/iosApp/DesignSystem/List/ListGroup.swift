//
//  ListGroup.swift
//  iosApp
//
//  Created by BookkMe on 03.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct ListGroup<T, S: ListStyle, Header: View, Content: View>:View where T:AnyObject, T:Identifiable {
	
	@Bindable
	var listState: IOSListState<T>
	
	let listStyle: S
	
	@ViewBuilder
	let content: (T) -> Content
	
	@ViewBuilder
	let header: () -> Header
	
	init(listState: IOSListState<T>, listStyle: S, @ViewBuilder content: @escaping (T) -> Content, @ViewBuilder header: @escaping () -> Header) {
		self._listState = Bindable(wrappedValue: listState)
		self.content = content
		self.header = header
		self.listStyle = listStyle
	}

	init(listState: IOSListState<T>, @ViewBuilder content: @escaping (T) -> Content) where Header == EmptyView, S == PlainListStyle {
		self.init(listState: listState, listStyle: .plain, content: content) { EmptyView() }
	}

	init(listState: IOSListState<T>, listStyle: S, @ViewBuilder content: @escaping (T) -> Content) where Header == EmptyView {
		self.init(listState: listState, listStyle: listStyle, content: content) { EmptyView() }
	}
	
	var body: some View {
		GeometryReader { geo in
			List {
				header()
				if (listState.items.isEmpty) {
					ZStack {
						if let errorState = listState.errorState {
							ListErrorView(state: errorState)
						} else if (listState.isInitialLoading) {
							ProgressView()
						} else if let emptyState = listState.emptyState {
							ListEmptyView(state: emptyState)
						}
					}
					.frame(maxWidth: .infinity, minHeight: 600, maxHeight: .infinity)
					.listRowInsets(EdgeInsets())
					.listRowBackground(Color.clear)
					.listRowSeparator(.hidden)
				} else {
					ForEach(listState.typedItems) { item in
						content(item)
							.onAppear {
								if item.id == listState.typedItems.last?.id {
									listState.loadMore?()
								}
							}
					}
				}
			}
			.listStyle(listStyle)
			.scrollDismissesKeyboard(.immediately)
		}
	}
}

