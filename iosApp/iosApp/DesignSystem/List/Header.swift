//
//  Header.swift
//  iosApp
//
//  Created by BookkMe on 22.09.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI

struct Header : View {
	@State
	var text: String = ""
	
	var body: some View {
		Text(text)
			.font(.footnote)
			.textCase(.uppercase)
			.foregroundStyle(AppColors.header)
			.padding(.leading)
	}
}
