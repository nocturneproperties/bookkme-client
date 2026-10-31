package me.bookk.feature.clients.domain.datasource

data class DeviceContact(
    val name: String,
    val lastName: String,
    val phones: List<String>,
    val emails: List<String>
)
