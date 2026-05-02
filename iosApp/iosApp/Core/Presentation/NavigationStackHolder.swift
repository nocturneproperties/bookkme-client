//
//  NavigationStack.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 29.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//

import SwiftUI
import Combine

@MainActor
public final class NavigationStackHolder: ObservableObject {
	
	@Published
	public var path = NavigationPath()
	private var pathMirror: [any Hashable] = []
	private var cancellables: Set<AnyCancellable> = []
	
	init(path: NavigationPath = NavigationPath()) {
		self.path = path
		cancellables.insert(
			$path.sink { [weak self] newPath in
				guard let self else { return }
				if pathMirror.count > newPath.count {
					pathMirror.removeLast(pathMirror.count - newPath.count)
				}
			}
		)
	}
	
	public func resetPath() {
		path = NavigationPath()
		pathMirror = []
	}
	
	public func push<T: Hashable>(_ destination: T) {
		path.append(destination)
		pathMirror.append(destination)
	}
	
	public func popLast() {
		guard path.count > 0 else { return }
		path.removeLast()
	}
}
