package me.bookk.designsystem.test

import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> failOnceThenSuspend(error: Throwable = TestException()): Flow<T> {
    var failed = false
    return flow {
        if (!failed) {
            failed = true
            throw error
        }
        awaitCancellation()
    }
}
