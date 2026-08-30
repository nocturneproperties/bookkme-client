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
    data class Business(
        val id: Uuid
    ) : BusinessDashboardSection(
        BusinessRes.strings.business_dashboard_business.desc(),
        listOf(
            Employees(id),
            Clients(id),
            Services(id),
            Analytics,
            Settings(id),
            Plugins(id)
        )
    ) {
        data class Employees(val id: Uuid) : DashboardUIItem(
            BusinessRes.strings.business_dashboard_employees.desc(),
            DashboardNavigationDestination.Employees(id)
        )

        data class Clients(val id: Uuid) : DashboardUIItem(
            BusinessRes.strings.business_dashboard_clients.desc(),
            DashboardNavigationDestination.Clients(id)
        )

        data object Analytics : DashboardUIItem(
            BusinessRes.strings.business_dashboard_analytics.desc(),
            DashboardNavigationDestination.Analytics
        )

        data class Settings(val id: Uuid) : DashboardUIItem(
            BusinessRes.strings.business_dashboard_settings.desc(),
            DashboardNavigationDestination.Settings(id)
        )

        data class Services(val id: Uuid) : DashboardUIItem(
            BusinessRes.strings.business_dashboard_services.desc(),
            DashboardNavigationDestination.Services(id)
        )

        data class Plugins(val id: Uuid) : DashboardUIItem(
            BusinessRes.strings.business_dashboard_plugins.desc(),
            DashboardNavigationDestination.Plugins(id)
        )
    }

    class Appointments(
        businessId: Uuid,
        items: List<DashboardUIItem> = listOf(
            History(businessId),
            Settings(businessId),
        )
    ) : BusinessDashboardSection(
        BusinessRes.strings.business_dashboard_appointments.desc(),
        items
    ) {
        data class History(val businessId: Uuid) : DashboardUIItem(
            BusinessRes.strings.business_dashboard_history.desc(),
            DashboardNavigationDestination.History(businessId)
        )

        data class Settings(val businessId: Uuid) : DashboardUIItem(
            BusinessRes.strings.business_dashboard_settings.desc(),
            DashboardNavigationDestination.AppointmentSettings(businessId)
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