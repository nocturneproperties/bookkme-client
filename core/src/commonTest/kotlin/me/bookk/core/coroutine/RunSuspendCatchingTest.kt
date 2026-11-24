package me.bookk.core.coroutine

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.yield
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class RunSuspendCatchingTest {

    @Test
    fun `returns the value of a block that succeeds`() = runUnitTest {
        given()
        val block = suspend {
            yield()
            42
        }

        whenn()
        val result = runSuspendCatching { block() }

        then()
        assertEquals(42, result.getOrThrow())
    }

    @Test
    fun `returns the error of a block that fails`() = runUnitTest {
        given()
        val block = suspend { throw IllegalStateException("broken") }

        whenn()
        val result = runSuspendCatching { block() }

        then()
        assertIs<IllegalStateException>(result.exceptionOrNull())
    }

    @Test
    fun `rethrows a cancellation`() = runUnitTest {
        given()
        val block = suspend { throw CancellationException("cancelled") }

        whenn()
        val call = suspend { runSuspendCatching { block() } }

        then()
        assertFailsWith<CancellationException> { call() }
    }
}
