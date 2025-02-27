package me.bookk.feature.settings.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
class ContactFormRemote(
    val text: String,
    val usageLogs: String?
)