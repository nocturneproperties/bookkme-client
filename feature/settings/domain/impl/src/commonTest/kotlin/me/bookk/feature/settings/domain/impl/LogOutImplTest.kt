package me.bookk.feature.settings.domain.impl

import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LogOutImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `calls doOnLogOut on all actors`() = runUnitTest {
        given()
        val actor1 = mockk<LogOutAction>()
        val actor2 = mockk<LogOutAction>()
        coJustRun { actor1.doOnLogOut() }
        coJustRun { actor2.doOnLogOut() }
        val sut = LogOutImpl(listOf(actor1, actor2))

        whenn()
        sut()

        then()
        coVerify { actor1.doOnLogOut() }
        coVerify { actor2.doOnLogOut() }
    }

    @Test
    fun `completes silently when actor throws`() = runUnitTest {
        given()
        val failingActor = mockk<LogOutAction>()
        coEvery { failingActor.doOnLogOut() } throws RuntimeException("network error")
        val sut = LogOutImpl(listOf(failingActor))

        whenn()
        sut()

        then()
    }

    @Test
    fun `completes successfully with empty actors list`() = runUnitTest {
        given()
        val sut = LogOutImpl(emptyList())

        whenn()
        sut()

        then()
    }
}
