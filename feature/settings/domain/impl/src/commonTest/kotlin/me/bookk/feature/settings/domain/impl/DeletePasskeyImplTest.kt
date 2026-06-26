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
import me.bookk.feature.settings.domain.datasource.passkey.PasskeySettingsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class DeletePasskeyImplTest {

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
        val dataSource = mockk<PasskeySettingsDataSource>()
        val sut = DeletePasskeyImpl(dataSource)
    }

    @Test
    fun `calls deletePasskey with correct id`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        coJustRun { fixture.dataSource.deletePasskey(id) }

        whenn()
        fixture.sut(id)

        then()
        coVerify { fixture.dataSource.deletePasskey(id) }
    }
}
