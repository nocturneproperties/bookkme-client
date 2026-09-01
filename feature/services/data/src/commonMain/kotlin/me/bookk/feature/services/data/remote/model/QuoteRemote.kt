package me.bookk.feature.services.data.remote.model

import kotlinx.serialization.Serializable
import me.bookk.feature.services.domain.api.quote.entity.Quote
import kotlin.uuid.Uuid

/**
 * Field order is significant: the API speaks protobuf and none of the remote models declare
 * explicit `@ProtoNumber`s, so field numbers are assigned by declaration order.
 */
@Serializable
internal class QuoteRequestRemote(
    val serviceIds: List<Uuid>
)

@Serializable
internal class QuoteRemote(
    val id: Uuid,
    val services: List<ServiceRemote>,
    val token: String
) {
    fun toDomain(): Quote {
        return Quote(
            id = id,
            services = services.map { it.toDomain() },
            token = token
        )
    }
}
