package me.bookk.feature.business.domain.api.entity

data class BusinessPermissions(
    val business: ResourcePermission,
    val employees: ResourcePermission,
    val clients: ResourcePermission,
    val services: ResourcePermission,
    val appointments: ResourcePermission
)
