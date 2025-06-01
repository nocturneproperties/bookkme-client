package me.bookk.feature.business.presentation.dashboard.state

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.feature.business.presentation.dashboard.DashboardNavigationDestination

abstract class DashboardUIItem(
    val text: StringDesc,
    val navigation: DashboardNavigationDestination
)

sealed class Section(
    val title: StringDesc,
    val items: List<DashboardUIItem>
) {
    class Business(
        items: List<DashboardUIItem> = listOf(
            Employees,
            Clients,
            Analytics,
            Settings
        )
    ) : Section(BusinessRes.strings.business_dashboard_business.desc(), items) {
        data object Employees : DashboardUIItem(
            BusinessRes.strings.business_dashboard_employees.desc(),
            DashboardNavigationDestination.Employees
        )

        data object Clients : DashboardUIItem(
            BusinessRes.strings.business_dashboard_clients.desc(),
            DashboardNavigationDestination.Clients
        )

        data object Analytics : DashboardUIItem(
            BusinessRes.strings.business_dashboard_analytics.desc(),
            DashboardNavigationDestination.Analytics
        )

        data object Settings : DashboardUIItem(
            BusinessRes.strings.business_dashboard_settings.desc(),
            DashboardNavigationDestination.Settings
        )
    }

    class Appointments(
        items: List<DashboardUIItem> = listOf(
            Services,
            History,
            Settings,
        )
    ) : Section(BusinessRes.strings.business_dashboard_appointments.desc(), items) {
        data object Services : DashboardUIItem(
            BusinessRes.strings.business_dashboard_services.desc(),
            DashboardNavigationDestination.Services
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
    ): Section(BusinessRes.strings.business_dashboard_shop.desc(), items) {
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