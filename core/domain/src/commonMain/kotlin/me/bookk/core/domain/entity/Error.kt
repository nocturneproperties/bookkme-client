package me.bookk.core.domain.entity

sealed class Error(val error: String, cause: Throwable?) : Throwable(error, cause) {
    class SimpleError(error: String, cause: Throwable) : Error(error, cause)
    class NoConnectionError(cause: Throwable) : Error("No Connection Error ", cause)
    class InternalServerError(cause: Throwable):Error("Internal Server Error", cause)
    class Cancelled(cause: Throwable) : Error("Cancelled", cause)
    class Unauthorized(cause: Throwable) : Error("Unauthorized", cause)
    class BadRequest(cause: Throwable) : Error("BadRequest", cause)

    class ApiError(
        error: String,
        cause: Exception,
        val errorCode: Int
    ) : Error(error, cause)
}

class ApiMessage(
    val message: String,
    val code: Int
)