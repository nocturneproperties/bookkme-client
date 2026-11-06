package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import library.crashreporting.api.CrashReporter
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GetCrashReportingEnabledImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class Fixture {
        val crashReporter = mock<CrashReporter>()
        val sut = GetCrashReportingEnabledImpl(crashReporter)
    }

    @Test
    fun `returns true when crash report collection is enabled`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.crashReporter.isCollectionEnabled() } returns true

        whenn()
        val result = fixture.sut()

        then()
        assertTrue(result)
    }

    @Test
    fun `returns false when crash report collection is disabled`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.crashReporter.isCollectionEnabled() } returns false

        whenn()
        val result = fixture.sut()

        then()
        assertFalse(result)
    }
}
