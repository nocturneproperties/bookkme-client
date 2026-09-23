package me.bookk.feature.business.domain.impl.plugin

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
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
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
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
    fun `flow emits the cached availability`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        every { fixture.pluginDataSource.observeAppointmentPluginAvailability(businessId) } returns flowOf(true)

        whenn()
        val result = fixture.sut.flow(businessId).first()

        then()
        assertEquals(true, result)
    }

    @Test
    fun `flow emits null when availability was never cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        every { fixture.pluginDataSource.observeAppointmentPluginAvailability(businessId) } returns flowOf(null)

        whenn()
        val result = fixture.sut.flow(businessId).first()

        then()
        assertNull(result)
    }

    @Test
    fun `flow relays a later availability change`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val availability = MutableSharedFlow<Boolean?>(replay = 1).apply { tryEmit(false) }
        every { fixture.pluginDataSource.observeAppointmentPluginAvailability(businessId) } returns availability
        val results = mutableListOf<Boolean?>()
        val job = launch(Dispatchers.Unconfined) { fixture.sut.flow(businessId).collect { results.add(it) } }

        whenn()
        availability.emit(true)

        then()
        job.cancel()
        assertEquals(listOf<Boolean?>(false, true), results)
    }

    @Test
    fun `refresh returns the availability fetched from remote`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.pluginDataSource.isAppointmentPluginAvailableOnRemote(businessId) } returns true
        everySuspend { fixture.pluginDataSource.saveAppointmentPluginAvailability(businessId, true) } returns Unit

        whenn()
        val result = fixture.sut.refresh(businessId)

        then()
        assertEquals(true, result)
    }

    @Test
    fun `refresh saves the fetched availability`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.pluginDataSource.isAppointmentPluginAvailableOnRemote(businessId) } returns false
        everySuspend { fixture.pluginDataSource.saveAppointmentPluginAvailability(businessId, false) } returns Unit

        whenn()
        fixture.sut.refresh(businessId)

        then()
        verifySuspend { fixture.pluginDataSource.saveAppointmentPluginAvailability(businessId, false) }
    }

    @Test
    fun `refresh propagates a fetch error without saving`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.pluginDataSource.isAppointmentPluginAvailableOnRemote(businessId) } throws IllegalStateException()

        whenn()
        val error = runCatching { fixture.sut.refresh(businessId) }.exceptionOrNull()

        then()
        assertFailsWith<IllegalStateException> { throw error!! }
        verifySuspend(VerifyMode.exactly(0)) { fixture.pluginDataSource.saveAppointmentPluginAvailability(any(), any()) }
    }
}
