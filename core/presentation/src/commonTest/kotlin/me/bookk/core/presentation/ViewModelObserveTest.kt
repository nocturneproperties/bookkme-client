package me.bookk.core.presentation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.setMain
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.error.ErrorDescription
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.time.Duration.Companion.seconds

class ViewModelObserveTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var originalMain: CoroutineDispatcher

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        originalMain = DispatcherProvider.main
        DispatcherProvider.swapMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        DispatcherProvider.swapMain(originalMain)
        Dispatchers.resetMain()
    }

    private class FakeErrorMapper : ErrorMapper {
        override fun mapToNotification(e: Throwable): PresentationNotification {
            return PresentationNotification.Ignore
        }

        override fun mapToDescription(e: Throwable): ErrorDescription {
            error("not used")
        }
    }

    private class TestViewModel : ViewModel(VmArgs(FakeErrorMapper())) {
        val handledErrors = mutableListOf<Throwable>()

        override fun handleError(throwable: Throwable) {
            handledErrors.add(throwable)
        }

        fun <T> start(flow: Flow<T>, action: suspend (T) -> Unit) {
            flow
                .safeOnEach(action)
                .observe()
        }

        fun <T> startWithOnError(flow: Flow<T>, onError: suspend (Throwable) -> Unit, action: suspend (T) -> Unit) {
            flow
                .safeOnEach(action)
                .onError(onError)
                .observe()
        }

        fun launchFailing(error: Throwable) {
            viewModelScope.launch { throw error }
        }

        fun stop() {
            viewModelScope.cancel()
        }
    }

    private class Fixture {
        val sut = TestViewModel()
        val reported = mutableListOf<Throwable>()
        val received = mutableListOf<Int>()
    }

    @Test
    fun `onError reports an upstream failure and observing continues`() = runUnitTest {
        given()
        val fixture = Fixture()
        val failure = IllegalStateException("db")
        var attempts = 0
        val upstream = flow {
            attempts++
            if (attempts == 1) throw failure
            emit(attempts)
        }

        whenn()
        fixture.sut.startWithOnError(upstream, onError = { fixture.reported.add(it) }) { fixture.received.add(it) }
        advanceTimeBy(1.seconds)
        runCurrent()

        then()
        fixture.sut.stop()
        assertEquals(listOf<Throwable>(failure), fixture.reported)
        assertEquals(listOf(2), fixture.received)
    }

    @Test
    fun `observe logs upstream failures through handleError`() = runUnitTest {
        given()
        val fixture = Fixture()
        val failure = IllegalStateException("db")

        whenn()
        fixture.sut.start(flow<Int> { throw failure }) {}
        runCurrent()

        then()
        fixture.sut.stop()
        assertSame(failure, fixture.sut.handledErrors.single())
    }

    @Test
    fun `safeOnEach routes action failures to handleError`() = runUnitTest {
        given()
        val fixture = Fixture()
        val failure = IllegalArgumentException("render")

        whenn()
        fixture.sut.startWithOnError(flow { emit(1) }, onError = { fixture.reported.add(it) }) { throw failure }
        runCurrent()

        then()
        fixture.sut.stop()
        assertSame(failure, fixture.sut.handledErrors.single())
        assertEquals(emptyList(), fixture.reported)
    }

    @Test
    fun `an uncaught failure in viewModelScope goes to handleError`() = runUnitTest {
        given()
        val fixture = Fixture()
        val failure = IllegalStateException("uncaught")

        whenn()
        fixture.sut.launchFailing(failure)
        runCurrent()

        then()
        fixture.sut.stop()
        assertSame(failure, fixture.sut.handledErrors.single())
    }
}
