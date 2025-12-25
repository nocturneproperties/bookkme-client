//
//  PlainList.swift
//  iosApp
//
//  Created by BookkMe on 07.09.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI

struct PlainList<Content: View>: View {

	@ViewBuilder
	let content: () -> Content
	
	init(@ViewBuilder content: @escaping () -> Content) {
		self.content = content
	}
	
	var body: some View {
		List {
			content()
		}
		.listSectionSpacing(8)
	}
}
