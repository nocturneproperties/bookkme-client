package me.bookk.feature.services.data.remote.api

import io.ktor.resources.Resource
import kotlin.uuid.Uuid

object ServiceRouting {

    @Resource("api")
    class Api {
        @Resource("/business/{businessId}/service")
        class Service(val parent: Api = Api(), val businessId: Uuid) {
            @Resource("/{id}")
            class Id(val parent: Service, val id: Uuid)

            @Resource("/quote")
            class Quote(val parent: Service)
        }

        @Resource("/business/{businessId}/service_group")
        class ServiceGroup(val parent: Api = Api(), val businessId: Uuid) {
            @Resource("/{id}")
            class Id(val parent: ServiceGroup, val id: Uuid)
        }
    }
}