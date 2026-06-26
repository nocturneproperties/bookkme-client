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
import me.bookk.feature.settings.domain.api.entity.ColorScheme
import me.bookk.feature.settings.domain.datasource.SettingsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateColorSchemeImplTest {

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
        val sut = UpdateColorSchemeImpl(dataSource)
    }

    @Test
    fun `calls setColorScheme with provided scheme`() = runUnitTest {
        given()
        val fixture = Fixture()
        coJustRun { fixture.dataSource.setColorScheme(ColorScheme.DARK) }

        whenn()
        fixture.fixture.invoke(ColorScheme.DARK)

        then()
        coVerify { fixture.dataSource.setColorScheme(ColorScheme.DARK) }
    }
}
