package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
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

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateCrashReportingEnabledImplTest {

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
        val crashReporter = mock<CrashReporter> {
            every { setCollectionEnabled(any()) } returns Unit
            every { deleteUnsentReports() } returns Unit
        }
        val sut = UpdateCrashReportingEnabledImpl(crashReporter)
    }

    @Test
    fun `disables collection before deleting pending reports when turned off`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut(enabled = false)

        then()
        verify(VerifyMode.order) {
            fixture.crashReporter.setCollectionEnabled(false)
            fixture.crashReporter.deleteUnsentReports()
        }
    }

    @Test
    fun `deletes reports stored while opted out before enabling collection`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut(enabled = true)

        then()
        verify(VerifyMode.order) {
            fixture.crashReporter.deleteUnsentReports()
            fixture.crashReporter.setCollectionEnabled(true)
        }
    }
}
