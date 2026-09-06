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
import kotlinx.datetime.LocalDate
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetAppointmentsForBusinessImplTest {

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
        val dataSource = mock<AppointmentDataSource>()
        val observeCurrentBusinessId = mock<ObserveCurrentBusinessId>()
        val sut = GetAppointmentsForBusinessImpl(dataSource, observeCurrentBusinessId)
    }

    private val date = LocalDate(2024, 1, 15)

    @Test
    fun `flow emits empty list when there is no current business`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.observeCurrentBusinessId() } returns flowOf(null)

        whenn()
        val result = fixture.sut.flow(date).first()

        then()
        assertEquals(emptyList(), result)
    }

    @Test
    fun `flow emits the db appointments for the current business and given date`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val appointments = listOf(stubAppointment(businessId = businessId))
        every { fixture.observeCurrentBusinessId() } returns flowOf(businessId)
        every { fixture.dataSource.observeAppointmentsForDateDBChanges(businessId, date) } returns flowOf(appointments)

        whenn()
        val result = fixture.sut.flow(date).first()

        then()
        assertEquals(appointments, result)
    }

    @Test
    fun `flow re-resolves the db observation when the current business changes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessIds = MutableSharedFlow<Uuid?>(replay = 1)
        val firstBusinessId = Uuid.random()
        val secondBusinessId = Uuid.random()
        val firstAppointments = listOf(stubAppointment(businessId = firstBusinessId))
        val secondAppointments = listOf(stubAppointment(businessId = secondBusinessId))
        businessIds.tryEmit(firstBusinessId)
        every { fixture.observeCurrentBusinessId() } returns businessIds
        every {
            fixture.dataSource.observeAppointmentsForDateDBChanges(firstBusinessId, date)
        } returns flowOf(firstAppointments)
        every {
            fixture.dataSource.observeAppointmentsForDateDBChanges(secondBusinessId, date)
        } returns flowOf(secondAppointments)
        val results = mutableListOf<List<Appointment>>()
        val job = launch(Dispatchers.Unconfined) {
            fixture.sut.flow(date).collect { results.add(it) }
        }

        whenn()
        businessIds.emit(secondBusinessId)

        then()
        job.cancel()
        assertEquals(firstAppointments, results.first())
        assertEquals(secondAppointments, results.last())
    }

    @Test
    fun `flow never triggers a network fetch as a side effect of being collected`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        every { fixture.observeCurrentBusinessId() } returns flowOf(businessId)
        every { fixture.dataSource.observeAppointmentsForDateDBChanges(businessId, date) } returns flowOf(emptyList())

        whenn()
        fixture.sut.flow(date).first()

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.getAppointmentsForDate(any(), any()) }
    }

    @Test
    fun `refresh fetches appointments from remote and saves them`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val appointments = listOf(stubAppointment(businessId = businessId))
        everySuspend { fixture.dataSource.getAppointmentsForDate(businessId, date) } returns appointments
        everySuspend {
            fixture.dataSource.getAppointmentIdsForDateInDb(businessId, date)
        } returns appointments.map { it.id }
        everySuspend { fixture.dataSource.saveAppointmentsInDB(appointments) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId, date) } returns Unit

        whenn()
        val result = fixture.sut.refresh(businessId, date)

        then()
        assertEquals(appointments, result)
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveAppointmentsInDB(appointments) }
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveLastSyncedAt(businessId, date) }
    }

    @Test
    fun `refresh deletes local appointments that are no longer present remotely`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val stillPresent = stubAppointment(businessId = businessId)
        val remote = listOf(stillPresent)
        val staleId = Uuid.random()
        everySuspend { fixture.dataSource.getAppointmentsForDate(businessId, date) } returns remote
        everySuspend {
            fixture.dataSource.getAppointmentIdsForDateInDb(businessId, date)
        } returns listOf(stillPresent.id, staleId)
        everySuspend { fixture.dataSource.deleteAppointmentsInDb(listOf(staleId)) } returns Unit
        everySuspend { fixture.dataSource.saveAppointmentsInDB(remote) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId, date) } returns Unit

        whenn()
        fixture.sut.refresh(businessId, date)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.deleteAppointmentsInDb(listOf(staleId)) }
    }

    @Test
    fun `refresh does not call delete when nothing is stale`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val appointment = stubAppointment(businessId = businessId)
        everySuspend { fixture.dataSource.getAppointmentsForDate(businessId, date) } returns listOf(appointment)
        everySuspend {
            fixture.dataSource.getAppointmentIdsForDateInDb(businessId, date)
        } returns listOf(appointment.id)
        everySuspend { fixture.dataSource.saveAppointmentsInDB(listOf(appointment)) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId, date) } returns Unit

        whenn()
        fixture.sut.refresh(businessId, date)

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.deleteAppointmentsInDb(any()) }
    }

    @Test
    fun `refresh propagates a fetch error to the caller`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val error = IllegalStateException("network down")
        everySuspend { fixture.dataSource.getAppointmentsForDate(businessId, date) } throws error

        whenn()
        val thrown = assertFailsWith<IllegalStateException> { fixture.sut.refresh(businessId, date) }

        then()
        assertEquals(error, thrown)
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.getAppointmentIdsForDateInDb(any(), any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveAppointmentsInDB(any()) }
    }
}
