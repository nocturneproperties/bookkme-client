package me.bookk.feature.business.presentation.screen.dashboard.state

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.feature.business.presentation.screen.dashboard.DashboardNavigationDestination
import kotlin.uuid.Uuid

abstract class DashboardUIItem(
    val text: StringDesc,
    val navigation: DashboardNavigationDestination
)

sealed class BusinessDashboardSection(
    val title: StringDesc,
    val items: List<DashboardUIItem>
) {
    class Business(
        id: Uuid,
        items: List<DashboardUIItem> = listOf(
            Employees,
            Clients(id),
            Services(id),
            Analytics,
            Settings(id)
        )
    ) : BusinessDashboardSection(BusinessRes.strings.business_dashboard_business.desc(), items) {
        data object Employees : DashboardUIItem(
            BusinessRes.strings.business_dashboard_employees.desc(),
            DashboardNavigationDestination.Employees
        )

        data class Clients(val id: Uuid) : DashboardUIItem(
            BusinessRes.strings.business_dashboard_clients.desc(),
            DashboardNavigationDestination.Clients(id)
        )

        data object Analytics : DashboardUIItem(
            BusinessRes.strings.business_dashboard_analytics.desc(),
            DashboardNavigationDestination.Analytics
        )

        class Settings(id: Uuid) : DashboardUIItem(
            BusinessRes.strings.business_dashboard_settings.desc(),
            DashboardNavigationDestination.Settings(id)
        )

        class Services(id: Uuid) : DashboardUIItem(
            BusinessRes.strings.business_dashboard_services.desc(),
            DashboardNavigationDestination.Services(id)
        )
    }

    class Appointments(
        items: List<DashboardUIItem> = listOf(
            Requests,
            History,
            Settings,
        )
    ) : BusinessDashboardSection(
        BusinessRes.strings.business_dashboard_appointments.desc(),
        items
    ) {
        data object Requests : DashboardUIItem(
            BusinessRes.strings.business_dashboard_requests.desc(),
            DashboardNavigationDestination.Requests
        )
        data object History : DashboardUIItem(
            BusinessRes.strings.business_dashboard_history.desc(),
            DashboardNavigationDestination.History
        )

        data object Settings : DashboardUIItem(
            BusinessRes.strings.business_dashboard_settings.desc(),
            DashboardNavigationDestination.AppointmentSettings
        )
    }


    class Shop(
        items: List<DashboardUIItem> = listOf(
            Assortment,
            Warehouse,
            Orders
        )
    ) : BusinessDashboardSection(BusinessRes.strings.business_dashboard_shop.desc(), items) {
        data object Assortment : DashboardUIItem(
            BusinessRes.strings.business_dashboard_assortment.desc(),
            DashboardNavigationDestination.Assortment
        )

        data object Warehouse : DashboardUIItem(
            BusinessRes.strings.business_dashboard_warehouse.desc(),
            DashboardNavigationDestination.Warehouse
        )

        data object Orders : DashboardUIItem(
            BusinessRes.strings.business_dashboard_orders.desc(),
            DashboardNavigationDestination.Orders
        )
    }

}