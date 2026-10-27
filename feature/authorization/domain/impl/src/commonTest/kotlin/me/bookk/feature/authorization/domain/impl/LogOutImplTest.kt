package me.bookk.feature.authorization.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
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
        val actor1 = mock<LogOutAction>()
        val actor2 = mock<LogOutAction>()
        everySuspend { actor1.doOnLogOut() } returns Unit
        everySuspend { actor2.doOnLogOut() } returns Unit
        val sut = LogOutImpl(listOf(actor1, actor2))

        whenn()
        sut()

        then()
        verifySuspend { actor1.doOnLogOut() }
        verifySuspend { actor2.doOnLogOut() }
    }

    @Test
    fun `completes silently when actor throws`() = runUnitTest {
        given()
        val failingActor = mock<LogOutAction>()
        everySuspend { failingActor.doOnLogOut() } throws RuntimeException("network error")
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
