package me.bookk.feature.appointments.domain.impl

import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
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
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
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
        val dataSource = mockk<AppointmentDataSource>()
        val sut = GetAppointmentsForBusinessImpl(dataSource)
    }

    @Test
    fun `returns appointments from datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val date = LocalDate(2024, 1, 15)
        val expected = listOf(stubAppointment(businessId = businessId))
        coEvery { fixture.dataSource.getAppointmentsForDate(businessId, date) } returns expected
        coJustRun { fixture.dataSource.saveAppointmentsInDB(expected) }

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
        coEvery { fixture.dataSource.getAppointmentsForDate(businessId, date) } returns appointments
        coJustRun { fixture.dataSource.saveAppointmentsInDB(appointments) }

        whenn()
        fixture.sut(businessId, date)

        then()
        coVerify(exactly = 1) { fixture.dataSource.saveAppointmentsInDB(appointments) }
    }

    @Test
    fun `returns empty list when datasource returns empty`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val date = LocalDate(2024, 1, 15)
        coEvery { fixture.dataSource.getAppointmentsForDate(businessId, date) } returns emptyList()
        coJustRun { fixture.dataSource.saveAppointmentsInDB(emptyList()) }

        whenn()
        val result = fixture.sut(businessId, date)

        then()
        assertEquals(emptyList(), result)
    }
}
