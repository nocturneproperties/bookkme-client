//
//  SwipeGesture.swift
//  iosApp
//
//  Created by BookkMe on 16.06.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI

extension UINavigationController: @retroactive UIGestureRecognizerDelegate {
	override open func viewDidLoad() {
		super.viewDidLoad()
		interactivePopGestureRecognizer?.delegate = self
	}
	
	public func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
		return viewControllers.count > 1 && presentedViewController == nil
	}
	
	public func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldBeRequiredToFailBy otherGestureRecognizer: UIGestureRecognizer) -> Bool {
		true
	}
}
