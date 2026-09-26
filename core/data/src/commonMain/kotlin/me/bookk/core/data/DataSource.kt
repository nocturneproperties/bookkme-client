package me.bookk.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import me.bookk.core.Logger
import me.bookk.core.data.map.toDomain
import me.bookk.core.domain.entity.Error

private val dataErrorLogger by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    Logger.create("DataLayerExceptions")
}

abstract class DataSource {

    companion object {
        const val DELETE_CHUNK_SIZE = 100
    }

    suspend fun <T> mapExceptions(
        exceptionMapper: ((Error) -> Throwable)? = null,
        finally: (() -> Unit)? = null,
        action: suspend () -> T
    ): T {
        return try {
            action()
        } catch (e: Exception) {
            dataErrorLogger.e(e)
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