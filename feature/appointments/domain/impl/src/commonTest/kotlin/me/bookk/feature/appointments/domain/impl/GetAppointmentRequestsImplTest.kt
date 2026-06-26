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
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.datasource.AppointmentRequestDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetAppointmentRequestsImplTest {

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
        val dataSource = mockk<AppointmentRequestDataSource>()
        val sut = GetAppointmentRequestsImpl(dataSource)
    }

    @Test
    fun `returns requests from datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val expected = listOf(stubAppointmentRequest(businessId = businessId))
        coEvery { fixture.dataSource.getAppointmentRequests(businessId) } returns expected
        coJustRun { fixture.dataSource.saveAppointmentRequestsInDB(expected) }

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(expected, result)
    }

    @Test
    fun `saves requests in DB after fetching`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val requests = listOf(stubAppointmentRequest())
        coEvery { fixture.dataSource.getAppointmentRequests(businessId) } returns requests
        coJustRun { fixture.dataSource.saveAppointmentRequestsInDB(requests) }

        whenn()
        fixture.sut(businessId)

        then()
        coVerify(exactly = 1) { fixture.dataSource.saveAppointmentRequestsInDB(requests) }
    }

    @Test
    fun `returns empty list and saves empty list`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        coEvery { fixture.dataSource.getAppointmentRequests(businessId) } returns emptyList()
        coJustRun { fixture.dataSource.saveAppointmentRequestsInDB(emptyList()) }

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(emptyList(), result)
        coVerify { fixture.dataSource.saveAppointmentRequestsInDB(emptyList()) }
    }
}
