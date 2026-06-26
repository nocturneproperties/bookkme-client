package me.bookk.feature.business.domain.impl.plugin

import io.mockk.coEvery
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
import me.bookk.feature.business.domain.datasource.PluginDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class IsAppointmentsPluginEnabledImplTest {

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
        val pluginDataSource = mockk<PluginDataSource>()
        val sut = IsAppointmentsPluginEnabledImpl(pluginDataSource)
    }

    @Test
    fun `returns true when plugin is available`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        coEvery { fixture.pluginDataSource.isAppointmentPluginAvailable(businessId) } returns true

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertTrue(result)
    }

    @Test
    fun `returns false when plugin is not available`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        coEvery { fixture.pluginDataSource.isAppointmentPluginAvailable(businessId) } returns false

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertFalse(result)
    }
}
