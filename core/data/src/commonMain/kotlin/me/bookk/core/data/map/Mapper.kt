package me.bookk.core.data.map

import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.utils.io.CancellationException
import me.bookk.core.data.BuildKonfig
import me.bookk.core.data.BusinessServerError
import me.bookk.core.domain.entity.Error

suspend fun Throwable.toDomain(): Error {
    return when (this) {
        is SocketTimeoutException,
        is ConnectTimeoutException -> Error.NoConnectionError(this)
        is CancellationException -> Error.Cancelled(this)
        is ResponseException -> {
            return when (response.status.value) {
                401 -> Error.Unauthorized(this)
                in 400..500 -> {
                    val body = runCatching { response.body<BusinessServerError>() }.getOrNull()
                    when {
                        body != null -> Error.BusinessError(body.errorCode, body.message)
                        response.status.value == 400 -> Error.BadRequest(this)
                        response.status.value == 500 -> Error.InternalServerError(this)
                        else -> Error.UnknownApiError(message.orEmpty(), this, response.status.value)
                    }
                }
                else -> Error.UnknownApiError(message.orEmpty(), this, response.status.value)
            }
        }

        else -> if (BuildKonfig.DEBUG) {
            Error.WrappedError(message.orEmpty(), this)
        } else {
            Error.Unknown(this)
        }
    }
}