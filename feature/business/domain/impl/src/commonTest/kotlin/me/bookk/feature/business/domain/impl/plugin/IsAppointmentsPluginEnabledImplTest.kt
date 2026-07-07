package me.bookk.feature.business.domain.impl.plugin

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
import me.bookk.feature.business.domain.datasource.PluginDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
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
        val pluginDataSource = mock<PluginDataSource>()
        val sut = IsAppointmentsPluginEnabledImpl(pluginDataSource)
    }

    @Test
    fun `returns true when plugin is available`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.pluginDataSource.isAppointmentPluginAvailableOnRemote(businessId) } returns true
        everySuspend { fixture.pluginDataSource.saveAppointmentPluginAvailability(businessId, true) } returns Unit

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
        everySuspend { fixture.pluginDataSource.isAppointmentPluginAvailableOnRemote(businessId) } returns false
        everySuspend { fixture.pluginDataSource.saveAppointmentPluginAvailability(businessId, false) } returns Unit

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertFalse(result)
    }

    @Test
    fun `saves fetched availability to the data source`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.pluginDataSource.isAppointmentPluginAvailableOnRemote(businessId) } returns true
        everySuspend { fixture.pluginDataSource.saveAppointmentPluginAvailability(businessId, true) } returns Unit

        whenn()
        fixture.sut(businessId)

        then()
        verifySuspend { fixture.pluginDataSource.saveAppointmentPluginAvailability(businessId, true) }
    }

    @Test
    fun `cached emits the cached value before the fetched value`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.pluginDataSource.getAppointmentPluginAvailability(businessId) } returns false
        everySuspend { fixture.pluginDataSource.isAppointmentPluginAvailableOnRemote(businessId) } returns true
        everySuspend { fixture.pluginDataSource.saveAppointmentPluginAvailability(businessId, true) } returns Unit
        val results = mutableListOf<Boolean>()

        whenn()
        fixture.sut.cached(businessId) { results.add(it) }

        then()
        assertEquals(listOf(false, true), results)
    }

    @Test
    fun `cached saves the freshly fetched availability to the data source`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.pluginDataSource.getAppointmentPluginAvailability(businessId) } returns false
        everySuspend { fixture.pluginDataSource.isAppointmentPluginAvailableOnRemote(businessId) } returns true
        everySuspend { fixture.pluginDataSource.saveAppointmentPluginAvailability(businessId, true) } returns Unit

        whenn()
        fixture.sut.cached(businessId) {}

        then()
        verifySuspend { fixture.pluginDataSource.saveAppointmentPluginAvailability(businessId, true) }
    }
}
