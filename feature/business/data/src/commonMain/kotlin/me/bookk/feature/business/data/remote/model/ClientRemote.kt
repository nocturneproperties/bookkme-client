package me.bookk.feature.business.data.remote.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
internal class ClientRemote(
    val id: Uuid,
    val name: String,
    val lastName: String,
    val phone: String,
    val userId: Uuid?,
)