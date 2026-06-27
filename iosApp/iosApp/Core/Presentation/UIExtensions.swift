//
//  UIExtensions.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 26.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//
import shared
import SwiftUI

extension Image {
	init(resource: shared.ImageResource?) {
		guard let uiImage = resource?.toUIImage() else {
			print("Warning: Couldn't convert ImageResource to UIImage")
			self.init(uiImage: UIImage())
			return
		}
		self.init(uiImage: uiImage)
	}
}

extension String {
	func nonEditableBinding() -> Binding<String> {
		return Binding(get: { self }, set: { _ in })
	}
}

extension KotlinUuid: @retroactive Identifiable {
	public var id: String { toHexString() }
}


