package me.bookk.feature.business.domain.api.entity

data class DashboardOverview(
    val business: Business,
    val features: Set<DashboardFeature>
)
