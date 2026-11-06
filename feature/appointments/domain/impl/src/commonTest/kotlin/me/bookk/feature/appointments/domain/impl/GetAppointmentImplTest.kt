package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
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
import me.bookk.feature.appointments.domain.api.GetAppointment
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class GetAppointmentImplTest {

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
        val sut = GetAppointmentImpl(dataSource)
    }

    @Test
    fun `returns appointment by id`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        val expected = stubAppointment(id = id)
        everySuspend { fixture.dataSource.getAppointment(id) } returns expected

        whenn()
        val result = fixture.sut(id)

        then()
        assertEquals(expected, result)
    }

    @Test
    fun `propagates datasource exception`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        everySuspend { fixture.dataSource.getAppointment(id) } throws
            IllegalStateException("not found")

        whenn()
        val thrown = runCatching { fixture.sut(id) }.exceptionOrNull()

        then()
        assertTrue(thrown is IllegalStateException)
    }

    @Test
    fun `refreshes exact appointment and saves the server result`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = stubAppointment()
        everySuspend { fixture.dataSource.getAppointmentFromRemote(appointment.id) } returns
            appointment
        everySuspend { fixture.dataSource.saveAppointmentInDB(appointment) } returns Unit

        whenn()
        val result = fixture.sut.refresh(appointment.id)

        then()
        assertEquals(appointment, result)
        verifySuspend { fixture.dataSource.saveAppointmentInDB(appointment) }
    }

    @Test
    fun `does not overwrite cached appointment when refresh fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        everySuspend { fixture.dataSource.getAppointmentFromRemote(id) } throws
            IllegalStateException("offline")

        whenn()
        val result = runCatching { fixture.sut.refresh(id) }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveAppointmentInDB(any()) }
    }

    @Test
    fun `maps missing or inaccessible appointment to not found`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        everySuspend { fixture.dataSource.getAppointmentFromRemote(id) } throws
            DomainError.NotFound(IllegalStateException())

        whenn()
        val result = runCatching { fixture.sut.refresh(id) }

        then()
        assertFailsWith<GetAppointment.Error.NotFound> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveAppointmentInDB(any()) }
    }

    @Test
    fun `propagates suspended access without overwriting cached appointment`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        everySuspend { fixture.dataSource.getAppointmentFromRemote(id) } throws
            DomainError.BusinessAccessSuspended("suspended")

        whenn()
        val result = runCatching { fixture.sut.refresh(id) }

        then()
        assertFailsWith<DomainError.BusinessAccessSuspended> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveAppointmentInDB(any()) }
    }

    @Test
    fun `propagates cache write failures after fetching fresh appointment`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = stubAppointment()
        everySuspend { fixture.dataSource.getAppointmentFromRemote(appointment.id) } returns
            appointment
        everySuspend { fixture.dataSource.saveAppointmentInDB(appointment) } throws
            IllegalStateException("write failed")

        whenn()
        val result = runCatching { fixture.sut.refresh(appointment.id) }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
    }
}
