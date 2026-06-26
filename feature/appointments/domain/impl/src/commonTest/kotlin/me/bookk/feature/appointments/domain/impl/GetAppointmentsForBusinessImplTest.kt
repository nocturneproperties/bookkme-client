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
        val dataSource = mock<AppointmentDataSource>()
        val sut = GetAppointmentsForBusinessImpl(dataSource)
    }

    @Test
    fun `returns appointments from datasource`() = runUnitTest {
        given()
        val sut = Fixture()
        val businessId = Uuid.random()
        val date = LocalDate(2024, 1, 15)
        val expected = listOf(stubAppointment(businessId = businessId))
        everySuspend { sut.dataSource.getAppointmentsForDate(businessId, date) } returns expected
        everySuspend { sut.dataSource.saveAppointmentsInDB(expected) } returns Unit

        whenn()
        val result = sut.sut(businessId, date)

        then()
        assertEquals(expected, result)
    }

    @Test
    fun `saves appointments in DB after fetching`() = runUnitTest {
        given()
        val sut = Fixture()
        val businessId = Uuid.random()
        val date = LocalDate(2024, 1, 15)
        val appointments = listOf(stubAppointment())
        everySuspend { sut.dataSource.getAppointmentsForDate(businessId, date) } returns appointments
        everySuspend { sut.dataSource.saveAppointmentsInDB(appointments) } returns Unit

        whenn()
        sut.sut(businessId, date)

        then()
        verifySuspend(VerifyMode.exactly(1)) { sut.dataSource.saveAppointmentsInDB(appointments) }
    }

    @Test
    fun `returns empty list when datasource returns empty`() = runUnitTest {
        given()
        val sut = Fixture()
        val businessId = Uuid.random()
        val date = LocalDate(2024, 1, 15)
        everySuspend { sut.dataSource.getAppointmentsForDate(businessId, date) } returns emptyList()
        everySuspend { sut.dataSource.saveAppointmentsInDB(emptyList()) } returns Unit

        whenn()
        val result = sut.sut(businessId, date)

        then()
        assertEquals(emptyList(), result)
    }
}
