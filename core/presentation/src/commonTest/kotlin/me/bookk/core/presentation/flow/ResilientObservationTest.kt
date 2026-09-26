package me.bookk.core.presentation.flow

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class ResilientObservationTest {

    private class UpstreamFailure(attempt: Int) : IllegalStateException("attempt $attempt")

    private class Fixture(
        private val scope: TestScope,
        val retryPolicy: RetryPolicy = RetryPolicy(initialDelay = 1.seconds, maxDelay = 30.seconds),
        private val onAttempt: suspend kotlinx.coroutines.flow.FlowCollector<Int>.(attempt: Int) -> Unit,
    ) {
        val subscriptionTimes = mutableListOf<Long>()
        val received = mutableListOf<Int>()
        val logged = mutableListOf<Throwable>()
        val reported = mutableListOf<Throwable>()

        val upstream: Flow<Int> = flow {
            subscriptionTimes.add(scope.testScheduler.currentTime)
            onAttempt(subscriptionTimes.size)
        }

        fun observe(action: suspend (Int) -> Unit = { received.add(it) }): Job {
            return upstream
                .safeOnEach(logError = { logged.add(it) }, action = action)
                .retryingWithBackoff(
                    retryPolicy = retryPolicy,
                    logError = { logged.add(it) },
                    onError = { reported.add(it) },
                )
                .observeResiliently(
                    scope = scope.backgroundScope,
                    retryPolicy = retryPolicy,
                    logError = { logged.add(it) },
                )
        }

        fun observeWithoutOnError(): Job {
            return upstream
                .safeOnEach(logError = { logged.add(it) }, action = { received.add(it) })
                .observeResiliently(
                    scope = scope.backgroundScope,
                    retryPolicy = retryPolicy,
                    logError = { logged.add(it) },
                )
        }
    }

    @Test
    fun `resubscribes after an upstream failure`() = runUnitTest {
        given()
        val fixture = Fixture(this) { attempt ->
            if (attempt == 1) throw UpstreamFailure(attempt)
            emit(attempt)
        }

        whenn()
        fixture.observe()
        advanceTimeBy(1.seconds)
        runCurrent()

        then()
        assertEquals(listOf(2), fixture.received)
    }

    @Test
    fun `doubles the delay between consecutive retries`() = runUnitTest {
        given()
        val fixture = Fixture(this) { attempt -> throw UpstreamFailure(attempt) }

        whenn()
        fixture.observe()
        advanceTimeBy(7.seconds)
        runCurrent()

        then()
        assertEquals(listOf(0L, 1_000L, 3_000L, 7_000L), fixture.subscriptionTimes)
    }

    @Test
    fun `caps the retry delay at the policy maximum`() = runUnitTest {
        given()
        val fixture = Fixture(
            scope = this,
            retryPolicy = RetryPolicy(initialDelay = 1.seconds, maxDelay = 3.seconds),
        ) { attempt -> throw UpstreamFailure(attempt) }

        whenn()
        fixture.observe()
        advanceTimeBy(9.seconds)
        runCurrent()

        then()
        assertEquals(listOf(0L, 1_000L, 3_000L, 6_000L, 9_000L), fixture.subscriptionTimes)
    }

    @Test
    fun `resets the retry delay after a successful emission`() = runUnitTest {
        given()
        val fixture = Fixture(this) { attempt ->
            if (attempt == 3) emit(attempt)
            throw UpstreamFailure(attempt)
        }

        whenn()
        fixture.observe()
        advanceTimeBy(4.seconds)
        runCurrent()

        then()
        assertEquals(listOf(0L, 1_000L, 3_000L, 4_000L), fixture.subscriptionTimes)
    }

    @Test
    fun `reports only the first failure of a streak`() = runUnitTest {
        given()
        val fixture = Fixture(this) { attempt -> throw UpstreamFailure(attempt) }

        whenn()
        fixture.observe()
        advanceTimeBy(7.seconds)
        runCurrent()

        then()
        assertEquals(1, fixture.reported.size)
        assertEquals("attempt 1", fixture.reported.single().message)
    }

    @Test
    fun `logs every upstream failure`() = runUnitTest {
        given()
        val fixture = Fixture(this) { attempt -> throw UpstreamFailure(attempt) }

        whenn()
        fixture.observe()
        advanceTimeBy(7.seconds)
        runCurrent()

        then()
        assertEquals(listOf("attempt 1", "attempt 2", "attempt 3", "attempt 4"), fixture.logged.map { it.message })
    }

    @Test
    fun `reports a new streak after a successful emission`() = runUnitTest {
        given()
        val fixture = Fixture(this) { attempt ->
            if (attempt == 2) emit(attempt)
            throw UpstreamFailure(attempt)
        }

        whenn()
        fixture.observe()
        advanceTimeBy(2.seconds)
        runCurrent()

        then()
        assertEquals(listOf("attempt 1", "attempt 2"), fixture.reported.map { it.message })
    }

    @Test
    fun `keeps collecting after a safeOnEach action fails`() = runUnitTest {
        given()
        val fixture = Fixture(this) {
            emit(1)
            emit(2)
            emit(3)
        }
        val renderFailure = IllegalArgumentException("render")

        whenn()
        fixture.observe { value ->
            if (value == 2) throw renderFailure
            fixture.received.add(value)
        }
        runCurrent()

        then()
        assertEquals(listOf(1, 3), fixture.received)
        assertSame(renderFailure, fixture.logged.single())
    }

    @Test
    fun `does not resubscribe or report when a safeOnEach action fails`() = runUnitTest {
        given()
        val fixture = Fixture(this) { attempt -> emit(attempt) }

        whenn()
        fixture.observe { throw IllegalArgumentException("render") }
        runCurrent()

        then()
        assertEquals(1, fixture.subscriptionTimes.size)
        assertTrue(fixture.reported.isEmpty())
    }

    @Test
    fun `does not retry when the upstream is cancelled`() = runUnitTest {
        given()
        val fixture = Fixture(this) { throw CancellationException("cancelled") }

        whenn()
        fixture.observe()
        runCurrent()

        then()
        assertEquals(1, fixture.subscriptionTimes.size)
        assertTrue(fixture.reported.isEmpty())
        assertTrue(fixture.logged.isEmpty())
    }

    @Test
    fun `observe alone resubscribes after an upstream failure`() = runUnitTest {
        given()
        val fixture = Fixture(this) { attempt ->
            if (attempt == 1) throw UpstreamFailure(attempt)
            emit(attempt)
        }

        whenn()
        fixture.observeWithoutOnError()
        advanceTimeBy(1.seconds)
        runCurrent()

        then()
        assertEquals(listOf(2), fixture.received)
    }

    @Test
    fun `observe alone logs every upstream failure`() = runUnitTest {
        given()
        val fixture = Fixture(this) { attempt -> throw UpstreamFailure(attempt) }

        whenn()
        fixture.observeWithoutOnError()
        advanceTimeBy(3.seconds)
        runCurrent()

        then()
        assertEquals(listOf("attempt 1", "attempt 2", "attempt 3"), fixture.logged.map { it.message })
    }
}
