//
//  BusinessTab.swift
//  iosApp
//
//  Created by BookkMe on 09.05.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct BusinessTab: View {
	@StateObject var navigationStack = NavigationStackHolder()

	var body: some View {
		NavigationStack(path: $navigationStack.path) {
			BusinessDashboardScreen()
				.navigationDestination(for: ClientsDestinations.ClientDetails.self) { type in
					ClientDetailsScreen(id: type.id)
				}
				.navigationDestination(for: ClientsDestinations.CreateClient.self) { type in
					CreateClientScreen(businessId: type.businessId)
				}
				.navigationDestination(for: ClientsDestinations.EditClient.self) { type in
					EditClientScreen(id: type.id)
				}
				.navigationDestination(for: EmployeesDestinations.InviteEmployee.self) { type in
					InviteEmployeeScreen(businessId: type.businessId)
				}
				.navigationDestination(for: ServicesDestination.AddService.self) { type in
					AddServiceScreen(businessId: type.businessId)
				}
				.navigationDestination(for: ServicesDestination.ServiceGroupList.self) { _ in
					ServiceGroupListScreen()
				}
				.navigationDestination(for: AppointmentsDestination.Details.self) { dest in
					AppointmentDetailsScreen(appointmentId: dest.appointmentId)
				}
				.navigationDestination(for: DashboardNavigationDestination.self) { type in
					switch type {
					case is DashboardNavigationDestination.Settings:
						BusinessSettingsScreen()
					case is DashboardNavigationDestination.Clients:
						ClientsListScreen()
					case is DashboardNavigationDestination.Employees:
						EmployeeListScreen()
					case is DashboardNavigationDestination.Services:
						ServiceListScreen()
					case let type as DashboardNavigationDestination.Plugins:
						BusinessPluginsScreen(businessId: type.id)
					case let type as DashboardNavigationDestination.AppointmentSettings:
						AppointmentSettingsScreen(businessId: type.businessId)
					case let type as DashboardNavigationDestination.History:
						AppointmentHistoryScreen(businessId: type.businessId)
					default:
						ProgressView()
					}
				}
		}.environmentObject(navigationStack)
	}
}
