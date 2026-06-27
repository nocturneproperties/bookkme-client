package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.settings.domain.api.entity.ColorScheme
import me.bookk.feature.settings.domain.datasource.SettingsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class GetColorSchemeImplTest {

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
        val sut = GetColorSchemeImpl(dataSource)
    }

    @Test
    fun `returns color scheme from datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getColorScheme() } returns ColorScheme.DARK

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(ColorScheme.DARK, result)
    }

    @Test
    fun `asFlow emits color scheme from datasource flow`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.dataSource.getColorSchemeFlow() } returns flowOf(ColorScheme.LIGHT)

        whenn()
        val result = fixture.sut.asFlow().first()

        then()
        assertEquals(ColorScheme.LIGHT, result)
    }
}
