package me.bookk.feature.settings.data.remote.api

import io.ktor.resources.Resource

internal object NotificationRouting {
    @Resource("api")
    class Api {
        @Resource("/notifications")
        class Notifications(val parent: Api = Api()) {
            @Resource("/settings")
            class Settings(val parent: Notifications = Notifications())
        }
    }
}
