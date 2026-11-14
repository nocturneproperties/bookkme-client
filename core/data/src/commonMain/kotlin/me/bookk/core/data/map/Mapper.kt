package me.bookk.core.data.map

import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.utils.io.CancellationException
import kotlinx.io.IOException
import me.bookk.core.data.BuildKonfig
import me.bookk.core.data.BusinessServerError
import me.bookk.core.data.ServerErrorCodes
import me.bookk.core.domain.entity.Error

suspend fun Throwable.toDomain(): Error {
    return when (this) {
        is IOException -> {
            Error.NoConnectionError(this)
        }

        is CancellationException -> {
            Error.Cancelled(this)
        }

        is ResponseException -> {
            when (response.status.value) {
                401 -> {
                    Error.Unauthorized(this)
                }

                in 400..500 -> {
                    val body = response.protobufBusinessError()
                    when {
                        body != null && body.isAccessSuspended(response.status.value) -> {
                            Error.BusinessAccessSuspended(body.message)
                        }

                        body != null -> {
                            Error.BusinessError(body.errorCode, body.message)
                        }

                        response.status.value == 400 -> {
                            Error.BadRequest(this)
                        }

                        response.status.value == 404 -> {
                            Error.NotFound(this)
                        }

                        response.status == HttpStatusCode.TooManyRequests -> {
                            Error.TooManyRequests(this)
                        }

                        response.status.value == 500 -> {
                            Error.InternalServerError(this)
                        }

                        else -> {
                            Error.UnknownApiError(
                                message.orEmpty(),
                                this,
                                response.status.value,
                            )
                        }
                    }
                }

                else -> {
                    Error.UnknownApiError(message.orEmpty(), this, response.status.value)
                }
            }
        }

        else -> {
            if (BuildKonfig.DEBUG) {
                Error.WrappedError(message.orEmpty(), this)
            } else {
                Error.Unknown(this)
            }
        }
    }
}

private suspend fun HttpResponse.protobufBusinessError(): BusinessServerError? {
    if (contentType()?.match(ContentType.Application.ProtoBuf) != true) {
        return null
    }
    return runCatching { body<BusinessServerError>() }.getOrNull()
}

private fun BusinessServerError.isAccessSuspended(status: Int): Boolean {
    return status == HttpStatusCode.Forbidden.value &&
        errorCode == ServerErrorCodes.BUSINESS_EMPLOYEE_ACCESS_SUSPENDED
}
