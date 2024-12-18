package me.bookk.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import me.bookk.core.map.toDomain

abstract class DataSource {
    suspend fun <T> execute(finally: () -> Unit = {}, action: suspend () -> T): T {
        return try {
            action()
        } catch (e: Exception) {
            throw e.toDomain()
        } finally {
            finally()
        }
    }

    fun <T> Flow<T>.handleErrors(): Flow<T> {
        return catch { e ->
            throw e.toDomain()
        }
    }
}