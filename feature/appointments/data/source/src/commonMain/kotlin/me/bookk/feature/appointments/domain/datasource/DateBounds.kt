package me.bookk.feature.appointments.domain.datasource

import kotlin.time.Instant

data class DateBounds(
    val from: Instant? = null,
    val to: Instant? = null,
)
