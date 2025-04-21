package me.bookk.feature.settings.data.remote.api

import io.ktor.resources.Resource

internal object AuthRouting {
    @Resource("api")
    class Api {

        @Resource("/auth")
        class Auth(val parent: Api = Api()) {
            @Resource("/passkey")
            class PassKey(val parent: Auth = Auth()) {
                @Resource("/add/challenge")
                class AddChallenge(val parent: PassKey = PassKey())

                @Resource("/add/finish")
                class AddFinish(val parent: PassKey = PassKey())

                @Resource("{id}")
                class Id(val parent: PassKey = PassKey(), val id: Long)
            }
        }
    }
}