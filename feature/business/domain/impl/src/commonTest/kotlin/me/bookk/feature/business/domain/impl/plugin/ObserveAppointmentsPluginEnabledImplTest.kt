package me.bookk.feature.business.domain.impl.plugin

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.entity.BusinessEvent
import me.bookk.feature.business.domain.api.entity.businessEvents
import me.bookk.feature.business.domain.datasource.PluginDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveAppointmentsPluginEnabledImplTest {

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
        val sut = ObserveAppointmentsPluginEnabledImpl(pluginDataSource)
    }

    @Test
    fun `emits cached availability then remote availability on initial collection`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.pluginDataSource.getAppointmentPluginAvailability(businessId) } returns false
        everySuspend { fixture.pluginDataSource.isAppointmentPluginAvailableOnRemote(businessId) } returns true
        everySuspend { fixture.pluginDataSource.saveAppointmentPluginAvailability(businessId, true) } returns Unit
        val results = mutableListOf<Boolean>()
        val job = launch(Dispatchers.Unconfined) {
            fixture.sut(businessId).collect { results.add(it) }
        }

        whenn()
        job.cancel()

        then()
        assertEquals(listOf(false, true), results)
    }

    @Test
    fun `refetches availability when a plugin state changed event is emitted`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.pluginDataSource.getAppointmentPluginAvailability(businessId) } returns false
        everySuspend { fixture.pluginDataSource.isAppointmentPluginAvailableOnRemote(businessId) } returns true
        everySuspend { fixture.pluginDataSource.saveAppointmentPluginAvailability(businessId, true) } returns Unit
        val job = launch(Dispatchers.Unconfined) {
            fixture.sut(businessId).collect { }
        }

        whenn()
        businessEvents.emit(BusinessEvent.PluginStateChanged)

        then()
        job.cancel()
        verifySuspend(VerifyMode.exactly(2)) {
            fixture.pluginDataSource.isAppointmentPluginAvailableOnRemote(businessId)
        }
    }
}
