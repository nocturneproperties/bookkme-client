package me.bookk.core.presentation

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
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
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.milliseconds

class ViewModelLaunchTest {

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
        val errors = mutableListOf<Throwable>()
        val terminated = mutableListOf<String>()

        fun load(name: String, call: suspend () -> Unit) {
            launch(
                key = LOAD_KEY,
                launchBehaviour = LaunchBehaviour.DropOldest,
                launchIn = DispatcherProvider.main,
                call = call,
                onError = { errors.add(it) },
                onTerminate = { terminated.add(name) },
            )
        }

        private companion object {
            const val LOAD_KEY = "load"
        }
    }

    @Test
    fun `does not report a launch replaced by a newer one as an error`() = runUnitTest {
        given()
        val viewModel = TestViewModel()
        viewModel.load("first") { CompletableDeferred<Unit>().await() }

        whenn()
        viewModel.load("second") {}

        then()
        assertEquals(emptyList(), viewModel.errors)
        assertEquals(listOf("first", "second"), viewModel.terminated)
    }

    @Test
    fun `reports a timed out call as an error`() = runUnitTest {
        given()
        val viewModel = TestViewModel()

        whenn()
        viewModel.load("timeout") {
            withTimeout(1.milliseconds) { CompletableDeferred<Unit>().await() }
        }
        testDispatcher.scheduler.advanceUntilIdle()

        then()
        assertIs<TimeoutCancellationException>(viewModel.errors.single())
    }
}
