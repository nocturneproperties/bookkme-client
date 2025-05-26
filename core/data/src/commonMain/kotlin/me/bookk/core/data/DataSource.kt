package me.bookk.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import me.bookk.core.data.map.toDomain
import me.bookk.core.domain.entity.Error

abstract class DataSource {

    suspend fun <T> mapExceptions(
        exceptionMapper: ((Error) -> Throwable)? = null,
        finally: (() -> Unit)? = null,
        action: suspend () -> T
    ): T {
        return try {
            action()
        } catch (e: Exception) {
            val domainError = e.toDomain()
            throw (exceptionMapper?.invoke(domainError) ?: domainError)
        } finally {
            finally?.invoke()
        }
    }

    fun <T> Flow<T>.mapErrors(): Flow<T> {
        return catch { e ->
            throw e.toDomain()
        }
    }
}