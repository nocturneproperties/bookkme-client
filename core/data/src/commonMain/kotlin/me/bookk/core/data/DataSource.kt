package me.bookk.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import me.bookk.core.domain.entity.Error
import me.bookk.core.map.toDomain

abstract class DataSource {

    suspend fun <T> mapExceptions(
        exceptionMapper: ((Error.BusinessError) -> Throwable)? = null,
        finally: (() -> Unit)? = null,
        action: suspend () -> T
    ): T {
        return try {
            action()
        } catch (e: Exception) {
            val domainError = e.toDomain()
            if (exceptionMapper != null && domainError is Error.BusinessError) {
                throw exceptionMapper.invoke(domainError)
            } else {
                throw domainError
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