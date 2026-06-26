package me.bookk.feature.settings.domain.impl

import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.settings.domain.datasource.SettingsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SendContactFormImplTest {

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
        val dataSource = mockk<SettingsDataSource>()
        val sut = SendContactFormImpl(dataSource)
    }

    @Test
    fun `calls sendContactForm with text and null logs`() = runUnitTest {
        given()
        val fixture = Fixture()
        val text = "Hello support"
        coJustRun { fixture.dataSource.sendContactForm(text, null) }

        whenn()
        fixture.sut(text, includeLogs = false)

        then()
        coVerify { fixture.dataSource.sendContactForm(text, null) }
    }

    @Test
    fun `passes null logs even when includeLogs is true`() = runUnitTest {
        given()
        val fixture = Fixture()
        val text = "Need help"
        coJustRun { fixture.dataSource.sendContactForm(text, null) }

        whenn()
        fixture.sut(text, includeLogs = true)

        then()
        coVerify { fixture.dataSource.sendContactForm(text, null) }
    }
}
