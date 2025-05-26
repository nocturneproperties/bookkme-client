package me.bookk.feature.business.data.remote.api

import io.ktor.resources.Resource

object BusinessRouting {
    @Resource("api")
    class Api {

        @Resource("/business")
        class Business(val parent: Api = Api())
    }
}