package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
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
        val sut = GetAppointmentsForBusinessImpl(dataSource)
    }

    @Test
    fun `returns appointments from datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val date = LocalDate(2024, 1, 15)
        val expected = listOf(stubAppointment(businessId = businessId))
        everySuspend { fixture.dataSource.getAppointmentsForDate(businessId, date) } returns expected
        everySuspend { fixture.dataSource.saveAppointmentsInDB(expected) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId, date) } returns Unit

        whenn()
        val result = fixture.sut(businessId, date)

        then()
        assertEquals(expected, result)
    }

    @Test
    fun `saves appointments in DB after fetching`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val date = LocalDate(2024, 1, 15)
        val appointments = listOf(stubAppointment())
        everySuspend { fixture.dataSource.getAppointmentsForDate(businessId, date) } returns appointments
        everySuspend { fixture.dataSource.saveAppointmentsInDB(appointments) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId, date) } returns Unit

        whenn()
        fixture.sut(businessId, date)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveAppointmentsInDB(appointments) }
    }

    @Test
    fun `saves last synced at after fetching`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val date = LocalDate(2024, 1, 15)
        val appointments = listOf(stubAppointment())
        everySuspend { fixture.dataSource.getAppointmentsForDate(businessId, date) } returns appointments
        everySuspend { fixture.dataSource.saveAppointmentsInDB(appointments) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId, date) } returns Unit

        whenn()
        fixture.sut(businessId, date)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveLastSyncedAt(businessId, date) }
    }

    @Test
    fun `returns empty list when datasource returns empty`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val date = LocalDate(2024, 1, 15)
        everySuspend { fixture.dataSource.getAppointmentsForDate(businessId, date) } returns emptyList()
        everySuspend { fixture.dataSource.saveAppointmentsInDB(emptyList()) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId, date) } returns Unit

        whenn()
        val result = fixture.sut(businessId, date)

        then()
        assertEquals(emptyList(), result)
    }

    @Test
    fun `cached emits db appointments then remote appointments when previously synced`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val date = LocalDate(2024, 1, 15)
        val cached = listOf(stubAppointment(businessId = businessId))
        val remote = listOf(stubAppointment(businessId = businessId), stubAppointment(businessId = businessId))
        everySuspend { fixture.dataSource.getLastSyncedAt(businessId, date) } returns Instant.fromEpochMilliseconds(1)
        everySuspend { fixture.dataSource.getAppointmentsForDateFromDb(businessId, date) } returns cached
        everySuspend { fixture.dataSource.getAppointmentsForDate(businessId, date) } returns remote
        everySuspend { fixture.dataSource.saveAppointmentsInDB(remote) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId, date) } returns Unit
        val emitted = mutableListOf<List<Appointment>>()

        whenn()
        fixture.sut.cached(businessId, date) { emitted.add(it) }

        then()
        assertEquals(listOf(cached, remote), emitted)
    }

    @Test
    fun `cached only emits remote appointments when never synced before`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val date = LocalDate(2024, 1, 15)
        val remote = listOf(stubAppointment(businessId = businessId))
        everySuspend { fixture.dataSource.getLastSyncedAt(businessId, date) } returns null
        everySuspend { fixture.dataSource.getAppointmentsForDate(businessId, date) } returns remote
        everySuspend { fixture.dataSource.saveAppointmentsInDB(remote) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId, date) } returns Unit
        val emitted = mutableListOf<List<Appointment>>()

        whenn()
        fixture.sut.cached(businessId, date) { emitted.add(it) }

        then()
        assertEquals(listOf(remote), emitted)
    }
}
