package me.bookk.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import me.bookk.core.data.map.toDomain
import me.bookk.core.domain.entity.Error

abstract class DataSource {

    suspend fun <T> mapExceptions(
        businessExceptionMapper: ((Error.BusinessError) -> Throwable)? = null,
        exceptionMapper: (Error) -> Throwable = { it },
        finally: (() -> Unit)? = null,
        action: suspend () -> T
    ): T {
        return try {
            action()
        } catch (e: Exception) {
            val domainError = e.toDomain()
            if (businessExceptionMapper != null && domainError is Error.BusinessError) {
                throw businessExceptionMapper.invoke(domainError)
            } else {
                throw exceptionMapper(domainError)
            }
        } finally {
            finally?.invoke()
        }
    }

    fun <T> Flow<T>.handleErrors(): Flow<T> {
        return catch { e ->
            throw e.toDomain()
        }
    }
}