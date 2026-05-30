package me.bookk.core.domain.entity

sealed class Error(val error: String, cause: Throwable?) : Throwable(error, cause) {
    class WrappedError(error: String, cause: Throwable) : Error(error, cause)
    class NoConnectionError(cause: Throwable) : Error("No Connection Error", cause)
    class InternalServerError(cause: Throwable) : Error("Internal Server Error", cause)
    class Cancelled(cause: Throwable) : Error("Cancelled", cause)
    class Unauthorized(cause: Throwable) : Error("Unauthorized", cause)
    class BadRequest(cause: Throwable) : Error("BadRequest", cause)
    class Ignore(cause: Throwable) : Error("Exception can be ignored", cause)
    class Unknown(cause: Throwable) : Error("Unknown error", cause)
    data object InvalidApplicationState : Error("Invalid app state", null)

    class BusinessError(
        val errorCode: Int,
        message: String
    ) : Error(message, null)

    class UnknownApiError(
        error: String,
        cause: Exception,
        val errorCode: Int
    ) : Error(error, cause)
}

fun Throwable.businessOrThrow() = (this as? Error.BusinessError) ?: throw this

suspend inline fun <T> Result<T>.onBusinessError(action: suspend (error: Error.BusinessError) -> Unit): Result<T> {
    return onFailure {
        action(it.businessOrThrow())
    }
}