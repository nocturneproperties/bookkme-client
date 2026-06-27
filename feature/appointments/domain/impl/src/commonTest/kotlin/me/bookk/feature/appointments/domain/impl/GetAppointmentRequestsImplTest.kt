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
        val dataSource = mock<AppointmentRequestDataSource>()
        val sut = GetAppointmentRequestsImpl(dataSource)
    }

    @Test
    fun `returns requests from datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val expected = listOf(stubAppointmentRequest(businessId = businessId))
        everySuspend { fixture.dataSource.getAppointmentRequests(businessId) } returns expected
        everySuspend { fixture.dataSource.saveAppointmentRequestsInDB(expected) } returns Unit

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
        everySuspend { fixture.dataSource.getAppointmentRequests(businessId) } returns requests
        everySuspend { fixture.dataSource.saveAppointmentRequestsInDB(requests) } returns Unit

        whenn()
        fixture.sut(businessId)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveAppointmentRequestsInDB(requests) }
    }

    @Test
    fun `returns empty list and saves empty list`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.dataSource.getAppointmentRequests(businessId) } returns emptyList()
        everySuspend { fixture.dataSource.saveAppointmentRequestsInDB(emptyList()) } returns Unit

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(emptyList(), result)
        verifySuspend { fixture.dataSource.saveAppointmentRequestsInDB(emptyList()) }
    }
}
