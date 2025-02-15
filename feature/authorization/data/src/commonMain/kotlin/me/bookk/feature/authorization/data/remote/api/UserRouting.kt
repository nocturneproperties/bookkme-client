package me.bookk.feature.authorization.data.remote.api

import io.ktor.resources.Resource

internal object UserRouting {
    @Resource("api")
    class Api {
        @Resource("/user")
        class User(val parent: Api = Api()) {
            @Resource("/me")
            class Me(val parent: User = User())
        }
    }
}