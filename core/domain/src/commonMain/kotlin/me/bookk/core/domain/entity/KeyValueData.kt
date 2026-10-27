package me.bookk.core.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class KeyValueData(
    val key: String,
    val value: String
)