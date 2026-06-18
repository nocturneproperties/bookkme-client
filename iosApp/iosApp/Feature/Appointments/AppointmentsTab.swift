//
//  AppointmentsTab.swift
//  iosApp
//
//  Created by BookkMe on 12.06.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//
import SwiftUI
import shared

struct AppointmentsTab: View {
	
	@StateObject var navigationStack = NavigationStackHolder()
	
	var body: some View {
		NavigationStack(path: $navigationStack.path) {
			AppointmentListScreen()
				.navigationDestination(for: AppointmentsDestination.Create.self) { dest in
					AppointmentCreateScreen(businessId: dest.businessId)
				}
				.navigationDestination(for: AppointmentsDestination.Details.self) { dest in
					AppointmentDetailsScreen(appointmentId: dest.appointmentId)
				}
		}.environmentObject(navigationStack)
	}
}
