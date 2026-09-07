package me.bookk.feature.business.presentation.screen.dashboard.state

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.feature.business.domain.api.entity.DashboardFeature
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
        features: Set<DashboardFeature>,
        items: List<DashboardUIItem> = buildList {
            if (features.contains(DashboardFeature.EMPLOYEES)) {
                add(Employees)
            }
            if (features.contains(DashboardFeature.CLIENTS)) {
                add(Clients)
            }
            if (features.contains(DashboardFeature.SERVICES)) {
                add(Services)
            }
            if (features.contains(DashboardFeature.BUSINESS)) {
                add(Analytics)
                add(Settings)
                add(Plugins(id))
            }
        }
    ) : BusinessDashboardSection(
        BusinessRes.strings.business_dashboard_business.desc(),
        items
    ) {
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

        data object Services : DashboardUIItem(
            BusinessRes.strings.business_dashboard_services.desc(),
            DashboardNavigationDestination.Services
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