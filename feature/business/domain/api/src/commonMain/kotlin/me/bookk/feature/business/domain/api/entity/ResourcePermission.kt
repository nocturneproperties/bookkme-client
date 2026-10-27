package me.bookk.feature.business.domain.api.entity

data class ResourcePermission(
    val view: Boolean = false,
    val update: Boolean = false,
    val delete: Boolean = false
)

enum class BusinessResource {
    BUSINESS,
    EMPLOYEES,
    CLIENTS,
    SERVICES,
    APPOINTMENTS
}
