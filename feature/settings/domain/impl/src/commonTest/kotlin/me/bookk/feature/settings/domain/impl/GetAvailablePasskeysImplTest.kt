package me.bookk.feature.settings.domain.impl

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.settings.domain.api.entity.Passkey
import me.bookk.feature.settings.domain.datasource.passkey.PasskeySettingsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetAvailablePasskeysImplTest {

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
        val sut = GetAvailablePasskeysImpl(dataSource)
    }

    private fun stubPasskey() = Passkey(
        id = Uuid.random(),
        name = "iPhone 15",
        isBackedUp = true,
        createdAt = LocalDateTime(2024, 1, 1, 0, 0),
        lastUsedAt = LocalDateTime(2024, 6, 1, 0, 0)
    )

    @Test
    fun `returns passkeys from datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val expected = listOf(stubPasskey())
        coEvery { fixture.dataSource.getPasskeys() } returns expected

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(expected, result)
    }

    @Test
    fun `returns empty list when no passkeys`() = runUnitTest {
        given()
        val fixture = Fixture()
        coEvery { fixture.dataSource.getPasskeys() } returns emptyList()

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(emptyList(), result)
    }
}
