package me.bookk.feature.appointments.domain.impl

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
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import me.bookk.feature.appointments.domain.datasource.AppointmentSettingsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetAppointmentSettingsImplTest {

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
        val dataSource = mock<AppointmentSettingsDataSource>()
        val sut = GetAppointmentSettingsImpl(dataSource)
    }

    @Test
    fun `flow emits the db value`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val settings = AppointmentSettings.stub(businessId)
        every {
            fixture.dataSource.observeAppointmentSettingsDBChanges(businessId)
        } returns flowOf(settings)

        whenn()
        val result = fixture.sut.flow(businessId).first()

        then()
        assertEquals(settings, result)
    }

    @Test
    fun `flow emits null when no settings exist yet for the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        every {
            fixture.dataSource.observeAppointmentSettingsDBChanges(businessId)
        } returns flowOf(null)

        whenn()
        val result = fixture.sut.flow(businessId).first()

        then()
        assertNull(result)
    }

    @Test
    fun `flow re-resolves when the db observation emits again`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val emissions = MutableSharedFlow<AppointmentSettings?>(replay = 1)
        val first = AppointmentSettings.stub(businessId)
        val second = AppointmentSettings.stub(businessId)
        emissions.tryEmit(first)
        every { fixture.dataSource.observeAppointmentSettingsDBChanges(businessId) } returns emissions
        val results = mutableListOf<AppointmentSettings?>()
        val job = launch(Dispatchers.Unconfined) {
            fixture.sut.flow(businessId).collect { results.add(it) }
        }

        whenn()
        emissions.emit(second)

        then()
        job.cancel()
        assertEquals(first, results.first())
        assertEquals(second, results.last())
    }

    @Test
    fun `flow never triggers a network fetch as a side effect of being collected`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        every {
            fixture.dataSource.observeAppointmentSettingsDBChanges(businessId)
        } returns flowOf(null)

        whenn()
        fixture.sut.flow(businessId).first()

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.getAppointmentSettings(any()) }
    }

    @Test
    fun `refresh fetches settings from remote and saves them`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val remote = AppointmentSettings.stub(businessId)
        everySuspend { fixture.dataSource.getAppointmentSettings(businessId) } returns remote
        everySuspend { fixture.dataSource.saveAppointmentSettingsInDB(remote) } returns Unit

        whenn()
        val result = fixture.sut.refresh(businessId)

        then()
        assertEquals(remote, result)
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveAppointmentSettingsInDB(remote) }
    }

    @Test
    fun `refresh propagates a fetch error to the caller`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val error = IllegalStateException("network down")
        everySuspend { fixture.dataSource.getAppointmentSettings(businessId) } throws error

        whenn()
        val thrown = assertFailsWith<IllegalStateException> { fixture.sut.refresh(businessId) }

        then()
        assertEquals(error, thrown)
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveAppointmentSettingsInDB(any()) }
    }
}
