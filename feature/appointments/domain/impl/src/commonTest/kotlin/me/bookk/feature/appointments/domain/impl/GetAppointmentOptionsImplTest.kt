package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import library.money.api.Money
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.GetAppointmentSettings
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.service.GetServices
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Instant
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
        val getAppointmentSettings = mock<GetAppointmentSettings>()
        val getClients = mock<GetClientsList>()
        val getServices = mock<GetServices>()
        val sut = GetAppointmentOptionsImpl(getAppointmentSettings, getClients, getServices)
    }

    private fun stubClient(businessId: Uuid) = Client.Detached(
        id = Uuid.random(),
        name = "John",
        lastName = "Doe",
        phone = "123",
        email = "john@example.com",
        businessId = businessId
    )

    private fun stubService(businessId: Uuid) = Service(
        id = Uuid.random(),
        businessId = businessId,
        group = ServiceGroup(id = Uuid.random(), businessId = businessId, name = "Group", createdAt = Instant.fromEpochMilliseconds(0)),
        name = "Haircut",
        duration = Duration.parse("30m"),
        price = Money(1000L, Money.SupportedCurrency.USD),
        isAvailable = true,
        createdAt = Instant.fromEpochMilliseconds(0)
    )

    @Test
    fun `returns options combining clients services and settings`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val settings = AppointmentSettings.stub(businessId)
        val clients = listOf(stubClient(businessId))
        val services = listOf(stubService(businessId))
        everySuspend { fixture.getAppointmentSettings.refresh(businessId) } returns settings
        everySuspend { fixture.getClients.refresh(businessId) } returns clients
        everySuspend { fixture.getServices.refresh(businessId) } returns services

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(settings, result.settings)
        assertEquals(1, result.clients.size)
        assertEquals(clients[0].id, result.clients[0].id)
        assertEquals(1, result.services.size)
        assertEquals(services[0].id, result.services[0].id)
    }

    @Test
    fun `maps client to ClientSnapshot correctly`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val client = stubClient(businessId)
        everySuspend { fixture.getAppointmentSettings.refresh(businessId) } returns AppointmentSettings.stub(businessId)
        everySuspend { fixture.getClients.refresh(businessId) } returns listOf(client)
        everySuspend { fixture.getServices.refresh(businessId) } returns emptyList()

        whenn()
        val result = fixture.sut(businessId)

        then()
        val snapshot = result.clients.first()
        assertEquals(client.id, snapshot.id)
        assertEquals(client.fullName, snapshot.fullName)
        assertEquals(client.phone, snapshot.phone)
        assertEquals(client.email, snapshot.email)
    }

    @Test
    fun `maps service to ServiceSnapshot correctly`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val service = stubService(businessId)
        everySuspend { fixture.getAppointmentSettings.refresh(businessId) } returns AppointmentSettings.stub(businessId)
        everySuspend { fixture.getClients.refresh(businessId) } returns emptyList()
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
