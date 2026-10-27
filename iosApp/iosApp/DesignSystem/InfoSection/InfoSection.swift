//
//  InfoSection.swift
//  iosApp
//
//  Created by BookkMe on 07.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

extension InfoLine: @retroactive Identifiable {}

struct InfoSection: View {
	
	let section: InfoLine
	
	var sectionContent: some View {
		VStack(alignment: .leading) {
			Header(text: section.title.localized(), discardDefaultPadding: true)
			Text(section.value.localized())
		}
		.frame(maxWidth: .infinity, alignment: .leading)
	}
	
	var body: some View {
		if let action = section.onClick {
			Button(action: action) {
				sectionContent
					.contentShape(Rectangle())
			}
			.buttonStyle(.plain)
		} else {
			sectionContent
		}
	}
}

