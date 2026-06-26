package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
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
        val dataSource = mock<SettingsDataSource>()
        val sut = SendContactFormImpl(dataSource)
    }

    @Test
    fun `calls sendContactForm with text and null logs`() = runUnitTest {
        given()
        val fixture = Fixture()
        val text = "Hello support"
        everySuspend { fixture.dataSource.sendContactForm(text, null) } returns Unit

        whenn()
        fixture.sut(text, includeLogs = false)

        then()
        verifySuspend { fixture.dataSource.sendContactForm(text, null) }
    }

    @Test
    fun `passes null logs even when includeLogs is true`() = runUnitTest {
        given()
        val fixture = Fixture()
        val text = "Need help"
        everySuspend { fixture.dataSource.sendContactForm(text, null) } returns Unit

        whenn()
        fixture.sut(text, includeLogs = true)

        then()
        verifySuspend { fixture.dataSource.sendContactForm(text, null) }
    }
}
