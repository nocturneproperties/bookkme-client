package me.bookk.feature.settings.data.remote.api

import io.ktor.resources.Resource

internal object UserRouting {
    @Resource("api")
    class Api {
        @Resource("/user")
        class User(
            val parent: Api = Api(),
        ) {
            @Resource("/legal")
            class Legal(
                val parent: User = User(),
            )

            @Resource("/me")
            class Me(
                val parent: User = User(),
            ) {
                @Resource("/legal")
                class Legal(
                    val parent: Me = Me(),
                )
            }

            @Resource("/export")
            class Export(
                val parent: User = User(),
            ) {
                @Resource("/download")
                class Download(
                    val parent: Export = Export(),
                )
            }
        }
    }
}
