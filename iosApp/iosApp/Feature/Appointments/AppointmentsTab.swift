//
//  AppointmentsTab.swift
//  iosApp
//
//  Created by BookkMe on 12.06.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//
import SwiftUI

struct AppointmentsTab: View {
	
	@StateObject var navigationStack = NavigationStackHolder()
	
	var body: some View {
		NavigationStack(path: $navigationStack.path) {
			AppointmentListScreen()
		}.environmentObject(navigationStack)
	}
}
