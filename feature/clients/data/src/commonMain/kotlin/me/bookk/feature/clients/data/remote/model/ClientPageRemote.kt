package me.bookk.feature.clients.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.domain.pagination.Page
import me.bookk.feature.clients.data.mapping.toDomain
import me.bookk.feature.clients.domain.api.entity.Client
import kotlin.uuid.Uuid

@Serializable
internal class ClientPageRemote(
    @ProtoNumber(1) val data: List<ClientRemote>,
    @ProtoNumber(2) val nextCursor: String? = null,
) {
    fun toDomain(businessId: Uuid): Page<Client> {
        return Page(items = data.map { it.toDomain(businessId) }, nextCursor = nextCursor)
    }
}
