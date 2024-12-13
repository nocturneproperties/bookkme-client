package me.bookk.core.map

import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.utils.io.CancellationException
import me.bookk.core.domain.entity.Error

fun Throwable.toDomain(): Error {
    return when (this) {
        is SocketTimeoutException,
        is ConnectTimeoutException -> Error.NoConnectionError(this)
        is ServerResponseException -> Error.InternalServerError(this)
        is CancellationException -> Error.Cancelled(this)
        is ClientRequestException -> {
            return when (response.status.value) {
                401 -> Error.Unauthorized(this)
                in 400..499 -> Error.BadRequest(this)
                else -> Error.ApiError(message, this, response.status.value)
            }
        }

        else -> Error.SimpleError(message.orEmpty(), this)
    }
}