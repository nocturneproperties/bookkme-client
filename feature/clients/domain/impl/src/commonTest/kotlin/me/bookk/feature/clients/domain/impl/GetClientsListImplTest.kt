package me.bookk.feature.clients.domain.impl

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
import kotlinx.datetime.TimeZone
import library.money.api.Currency
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.datasource.ClientsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetClientsListImplTest {

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
        val dataSource = mock<ClientsDataSource>()
        val observeDashboardBusinessChanges = mock<ObserveDashboardBusinessChanges>()
        val sut = GetClientsListImpl(dataSource, observeDashboardBusinessChanges)
    }

    private fun stubBusiness(id: Uuid = Uuid.random()) = Business(
        id = id,
        name = "Business",
        description = "",
        address = "",
        location = null,
        currency = Currency("USD"),
        timeZone = TimeZone.UTC,
        socials = emptyMap(),
        schedule = WorkingSchedule(),
        permissions = BusinessPermissions(
            business = ResourcePermission(),
            employees = ResourcePermission(),
            clients = ResourcePermission(),
            services = ResourcePermission(),
            appointments = ResourcePermission()
        )
    )

    private fun stubClient(businessId: Uuid) = Client.Detached(
        id = Uuid.random(),
        name = "John",
        lastName = "Doe",
        phone = "123",
        email = "j@e.com",
        businessId = businessId
    )

    @Test
    fun `flow emits empty list when there is no dashboard business`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(null)

        whenn()
        val result = fixture.sut.flow().first()

        then()
        assertEquals(emptyList(), result)
    }

    @Test
    fun `flow emits the db clients for the current dashboard business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        val clients = listOf(stubClient(business.id))
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(business)
        every { fixture.dataSource.observeClientsDBChanges(business.id) } returns flowOf(clients)

        whenn()
        val result = fixture.sut.flow().first()

        then()
        assertEquals(clients, result)
    }

    @Test
    fun `flow re-resolves the db observation when the dashboard business changes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businesses = MutableSharedFlow<Business?>(replay = 1)
        val firstBusiness = stubBusiness()
        val secondBusiness = stubBusiness()
        val firstClients = listOf(stubClient(firstBusiness.id))
        val secondClients = listOf(stubClient(secondBusiness.id))
        businesses.tryEmit(firstBusiness)
        every { fixture.observeDashboardBusinessChanges() } returns businesses
        every { fixture.dataSource.observeClientsDBChanges(firstBusiness.id) } returns flowOf(firstClients)
        every { fixture.dataSource.observeClientsDBChanges(secondBusiness.id) } returns flowOf(secondClients)
        val results = mutableListOf<List<Client>>()
        val job = launch(Dispatchers.Unconfined) {
            fixture.sut.flow().collect { results.add(it) }
        }

        whenn()
        businesses.emit(secondBusiness)

        then()
        job.cancel()
        assertEquals(firstClients, results.first())
        assertEquals(secondClients, results.last())
    }

    @Test
    fun `flow never triggers a network fetch as a side effect of being collected`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(business)
        every { fixture.dataSource.observeClientsDBChanges(business.id) } returns flowOf(emptyList())

        whenn()
        fixture.sut.flow().first()

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.getClients(any()) }
    }

    @Test
    fun `refresh fetches clients from remote and replaces the db contents`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val clients = listOf(stubClient(businessId))
        everySuspend { fixture.dataSource.getClients(businessId) } returns clients
        everySuspend { fixture.dataSource.deleteClientsInDb(businessId) } returns Unit
        everySuspend { fixture.dataSource.saveClientsInDb(clients) } returns Unit

        whenn()
        val result = fixture.sut.refresh(businessId)

        then()
        assertEquals(clients, result)
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.deleteClientsInDb(businessId) }
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveClientsInDb(clients) }
    }

    @Test
    fun `refresh propagates a fetch error to the caller`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val error = IllegalStateException("network down")
        everySuspend { fixture.dataSource.getClients(businessId) } throws error

        whenn()
        val thrown = assertFailsWith<IllegalStateException> { fixture.sut.refresh(businessId) }

        then()
        assertEquals(error, thrown)
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.deleteClientsInDb(any()) }
    }
}
