package me.bookk.feature.settings.data.remote.api

import io.ktor.resources.Resource

internal object UserRouting {
    @Resource("api")
    class Api {
        @Resource("/user")
        class User(val parent: Api = Api()) {
            @Resource("/contactus")
            class ContactUs(val parent: User = User())
        }
    }
}