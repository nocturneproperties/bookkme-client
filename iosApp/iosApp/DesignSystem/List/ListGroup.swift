//
//  ListGroup.swift
//  iosApp
//
//  Created by BookkMe on 03.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct ListGroup<T, S: ListStyle, Header: View, Content: View>: View where T: AnyObject, T: Identifiable {

	let listState: IOSListState<T>
	let listStyle: S
	let content: (T) -> Content
	let header: () -> Header

	@State private var placeholderAreaTop: CGFloat = 0
	@State private var headerBottom: CGFloat = 0

	init(listState: IOSListState<T>, listStyle: S, @ViewBuilder content: @escaping (T) -> Content, @ViewBuilder header: @escaping () -> Header) {
		self.listState = listState
		self.listStyle = listStyle
		self.content = content
		self.header = header
	}

	init(listState: IOSListState<T>, @ViewBuilder content: @escaping (T) -> Content) where Header == EmptyView, S == PlainListStyle {
		self.init(listState: listState, listStyle: .plain, content: content) { EmptyView() }
	}

	init(listState: IOSListState<T>, listStyle: S, @ViewBuilder content: @escaping (T) -> Content) where Header == EmptyView {
		self.init(listState: listState, listStyle: listStyle, content: content) { EmptyView() }
	}

	var body: some View {
		List {
			if let bannerError = listState.bannerError {
				Section {
					ListBannerErrorView(state: bannerError)
				}
				.listRowInsets(EdgeInsets())
				.listRowBackground(Color.clear)
				.listRowSeparator(.hidden)
				.listSectionSeparator(.hidden)
				.listSectionMargins(.horizontal, 0)
				.transition(.move(edge: .top).combined(with: .opacity))
			}

			header()
				.background(HeaderRowBottomProbe())

			if !showsPlaceholder {
				ForEach(listState.typedItems) { item in
					content(item)
				}
				if let loadMore = listState.loadMore {
					LoadMoreTrigger(itemCount: listState.typedItems.count, loadMore: loadMore)
				}
			}
		}
		.listStyle(listStyle)
		.onPreferenceChange(HeaderBottomKey.self) { headerBottom = $0 }
		.overlay(alignment: .top) {
			Color.clear
				.onGeometryChange(for: CGFloat.self) { $0.frame(in: .global).minY } action: { placeholderAreaTop = $0 }
				.overlay(alignment: .top) {
					placeholder
						.frame(maxWidth: .infinity, maxHeight: .infinity)
						.padding(.top, placeholderTopInset)
				}
		}
		.animation(.easeInOut(duration: 0.25), value: listState.bannerError != nil)
		.scrollDismissesKeyboard(.immediately)
	}

	private var placeholderTopInset: CGFloat {
		max(0, headerBottom - placeholderAreaTop)
	}

	private var showsPlaceholder: Bool {
		listState.errorState != nil || listState.typedItems.isEmpty
	}

	@ViewBuilder
	private var placeholder: some View {
		if let errorState = listState.errorState {
			ListErrorView(state: errorState)
		} else if listState.typedItems.isEmpty {
			if listState.isInitialLoading {
				ProgressView()
			} else if let emptyState = listState.emptyState {
				ListEmptyView(state: emptyState)
			}
		}
	}
}

private struct LoadMoreTrigger: View {
	let itemCount: Int
	let loadMore: () -> Void

	var body: some View {
		Color.clear
			.frame(height: 1)
			.listRowInsets(EdgeInsets())
			.listRowBackground(Color.clear)
			.listRowSeparator(.hidden)
			.id(itemCount)
			.onAppear(perform: loadMore)
	}
}

private struct HeaderBottomKey: PreferenceKey {
	static let defaultValue: CGFloat = 0

	static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
		value = max(value, nextValue())
	}
}

private struct HeaderRowBottomProbe: View {
	var body: some View {
		GeometryReader { proxy in
			Color.clear.preference(key: HeaderBottomKey.self, value: proxy.frame(in: .global).maxY)
		}
	}
}
