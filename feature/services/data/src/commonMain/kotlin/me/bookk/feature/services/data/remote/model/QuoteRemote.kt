package me.bookk.feature.services.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.services.domain.api.quote.entity.Quote
import kotlin.uuid.Uuid

@Serializable
internal class QuoteRequestRemote(
    @ProtoNumber(1) val serviceIds: List<Uuid>
)

@Serializable
internal class QuoteRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val services: List<ServiceRemote>,
    @ProtoNumber(3) val token: String
) {
    fun toDomain(): Quote {
        return Quote(
            id = id,
            services = services.map { it.toDomain() },
            token = token
        )
    }
}
