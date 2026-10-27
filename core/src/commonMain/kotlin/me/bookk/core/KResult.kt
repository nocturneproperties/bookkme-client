package me.bookk.core

sealed interface KResult<out T> {
    data class Success<T>(val value: T) : KResult<T>
    data class Failure(val error: Throwable) : KResult<Nothing>
}

fun <T> Result<T>.asKResult(): KResult<T> {
    if (isSuccess) {
        return KResult.Success(getOrThrow())
    }
    return KResult.Failure(requireNotNull(exceptionOrNull()))
}

fun <T> KResult<T>.asResult(): Result<T> {
    return when (this) {
        is KResult.Failure -> Result.failure(error)
        is KResult.Success<T> -> Result.success(value)
    }
}

fun <T> KResult<T>.getOrThrow(): T {
    return asResult().getOrThrow()
}