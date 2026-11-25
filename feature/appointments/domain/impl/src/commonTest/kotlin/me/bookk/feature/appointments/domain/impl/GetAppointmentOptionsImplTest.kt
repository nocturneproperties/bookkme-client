package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.services.domain.api.service.GetServices
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetAppointmentOptionsImplTest {

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
        val getServices = mock<GetServices>()
        val sut = GetAppointmentOptionsImpl(getServices)
    }

    @Test
    fun `returns options of the refreshed services`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val services = listOf(stubService(businessId))
        everySuspend { fixture.getServices.refresh(businessId) } returns services

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(1, result.services.size)
        assertEquals(services[0].id, result.services[0].id)
    }

    @Test
    fun `maps service to ServiceSnapshot correctly`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val service = stubService(businessId)
        everySuspend { fixture.getServices.refresh(businessId) } returns listOf(service)

        whenn()
        val result = fixture.sut(businessId)

        then()
        val snapshot = result.services.first()
        assertEquals(service.id, snapshot.id)
        assertEquals(service.name, snapshot.name)
        assertEquals(service.group.id, snapshot.groupId)
        assertEquals(service.price, snapshot.price)
        assertEquals(service.duration, snapshot.duration)
    }
}
