package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
enum class HistoryOrderRemote {
    NEWEST_FIRST,
    SOONEST_FIRST,
}
