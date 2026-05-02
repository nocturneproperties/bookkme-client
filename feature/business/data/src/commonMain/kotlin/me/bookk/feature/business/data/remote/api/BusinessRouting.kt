package me.bookk.feature.business.data.remote.api

import io.ktor.resources.Resource
import kotlin.uuid.Uuid

object BusinessRouting {
    @Resource("api")
    class Api {

        @Resource("/business")
        class Business(val parent: Api = Api()) {
            @Resource("/{id}")
            class Id(val parent: Business = Business(), val id: Uuid)
        }

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