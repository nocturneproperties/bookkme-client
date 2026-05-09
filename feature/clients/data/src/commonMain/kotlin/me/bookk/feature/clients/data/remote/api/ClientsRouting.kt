package me.bookk.feature.clients.data.remote.api

import io.ktor.resources.Resource
import kotlin.uuid.Uuid

object ClientsRouting {
    @Resource("api")
    class Api {

        @Resource("/business/{businessId}/clients")
        class Clients(val parent: Api = Api(), val businessId: Uuid) {
            @Resource("/{id}")
            class Id(
                val parent: Clients,
                val id: Uuid,
            )
        }
    }
}