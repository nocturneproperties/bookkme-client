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
import me.bookk.core.domain.entity.Error
import me.bookk.core.domain.pagination.Page
import me.bookk.core.domain.pagination.PageErrorCodes
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
import me.bookk.feature.clients.domain.api.entity.ClientsQuery
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
        val businessId = Uuid.random()
        val dataSource = mock<ClientsDataSource>()
        val observeDashboardBusinessChanges = mock<ObserveDashboardBusinessChanges>()
        val sut = GetClientsListImpl(dataSource, observeDashboardBusinessChanges)

        fun stubCached(vararg clients: Client) {
            val business = stubBusiness(businessId)
            every { observeDashboardBusinessChanges() } returns flowOf(business)
            every { dataSource.observeClientsDBChanges(businessId) } returns
                flowOf(clients.toList())
        }

        fun stubPage(search: String?, cursor: String?, page: Page<Client>) {
            everySuspend { dataSource.getClients(businessId, search, cursor) } returns page
        }

        fun stubWrites(cachedIds: List<Uuid> = emptyList()) {
            everySuspend { dataSource.getClientIdsInDb(businessId) } returns cachedIds
            everySuspend { dataSource.deleteClientsInDb(any()) } returns Unit
            everySuspend { dataSource.saveClientsInDb(any()) } returns Unit
            everySuspend { dataSource.saveLastSyncedAt(any()) } returns Unit
        }

        fun client(
            name: String = "John",
            lastName: String = "Doe",
            phone: String? = "123",
            email: String? = "j@e.com",
        ) = Client.Detached(
            id = Uuid.random(),
            name = name,
            lastName = lastName,
            phone = phone,
            email = email,
            businessId = businessId,
        )
    }

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
        every { fixture.dataSource.observeClientsDBChanges(firstBusiness.id) } returns
            flowOf(firstClients)
        every { fixture.dataSource.observeClientsDBChanges(secondBusiness.id) } returns
            flowOf(secondClients)
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
        every { fixture.dataSource.observeClientsDBChanges(business.id) } returns
            flowOf(emptyList())

        whenn()
        fixture.sut.flow().first()

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.getClients(any(), any(), any()) }
    }

    @Test
    fun `flow without a search emits every cached client`() = runUnitTest {
        given()
        val fixture = Fixture()
        val clients = arrayOf(fixture.client(name = "Anna"), fixture.client(name = "Olga"))
        fixture.stubCached(*clients)

        whenn()
        val result = fixture.sut.flow(search = "  ").first()

        then()
        assertEquals(clients.toList(), result)
    }

    @Test
    fun `flow with a search keeps clients whose names start with every searched word`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val anna = fixture.client(name = "Anna", lastName = "Kowalska")
            val annaNowak = fixture.client(name = "Anna", lastName = "Nowak")
            fixture.stubCached(anna, annaNowak)

            whenn()
            val result = fixture.sut.flow(search = "kow an").first()

            then()
            assertEquals(listOf(anna), result)
        }

    @Test
    fun `flow with a search ignores case, accents and apostrophes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val zoe = fixture.client(name = "Zoë", lastName = "O'Neil")
        fixture.stubCached(zoe, fixture.client(name = "Zack"))

        whenn()
        val result = fixture.sut.flow(search = "ZOE oneil").first()

        then()
        assertEquals(listOf(zoe), result)
    }

    @Test
    fun `flow with a search does not match the middle of a name`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubCached(fixture.client(name = "Joanna", email = null))

        whenn()
        val result = fixture.sut.flow(search = "anna").first()

        then()
        assertEquals(emptyList(), result)
    }

    @Test
    fun `flow with a search keeps clients whose email contains it`() = runUnitTest {
        given()
        val fixture = Fixture()
        val client = fixture.client(email = "Anna.K@Example.com")
        fixture.stubCached(client, fixture.client(email = "olga@example.com"))

        whenn()
        val result = fixture.sut.flow(search = "anna.k@ex").first()

        then()
        assertEquals(listOf(client), result)
    }

    @Test
    fun `flow with a phone search compares digits only`() = runUnitTest {
        given()
        val fixture = Fixture()
        val client = fixture.client(phone = "+48 501-234-567")
        fixture.stubCached(client, fixture.client(phone = "+48 600 100 200"))

        whenn()
        val result = fixture.sut.flow(search = "(501) 234").first()

        then()
        assertEquals(listOf(client), result)
    }

    @Test
    fun `refresh without a search replaces the cached clients with the first page`() = runUnitTest {
        given()
        val fixture = Fixture()
        val kept = fixture.client()
        val staleId = Uuid.random()
        val page = Page(listOf<Client>(kept), nextCursor = "after-1")
        fixture.stubPage(search = null, cursor = null, page)
        fixture.stubWrites(cachedIds = listOf(kept.id, staleId))

        whenn()
        val result = fixture.sut.refresh(ClientsQuery(fixture.businessId))

        then()
        assertEquals(page, result)
        verifySuspend(VerifyMode.order) {
            fixture.dataSource.deleteClientsInDb(listOf(staleId))
            fixture.dataSource.saveClientsInDb(page.items)
            fixture.dataSource.saveLastSyncedAt(fixture.businessId)
        }
    }

    @Test
    fun `refresh does not call delete when nothing is stale`() = runUnitTest {
        given()
        val fixture = Fixture()
        val client = fixture.client()
        fixture.stubPage(search = null, cursor = null, Page(listOf(client), nextCursor = null))
        fixture.stubWrites(cachedIds = listOf(client.id))

        whenn()
        fixture.sut.refresh(ClientsQuery(fixture.businessId))

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.deleteClientsInDb(any()) }
    }

    @Test
    fun `refresh with a search adds the found clients without removing others`() = runUnitTest {
        given()
        val fixture = Fixture()
        val found = fixture.client(name = "Anna")
        val page = Page(listOf<Client>(found), nextCursor = null)
        fixture.stubPage(search = "anna", cursor = null, page)
        fixture.stubWrites(cachedIds = listOf(Uuid.random()))

        whenn()
        val result = fixture.sut.refresh(ClientsQuery(fixture.businessId, search = "anna"))

        then()
        assertEquals(page, result)
        verifySuspend { fixture.dataSource.saveClientsInDb(page.items) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.deleteClientsInDb(any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveLastSyncedAt(any()) }
    }

    @Test
    fun `refresh propagates a fetch error and keeps the cache`() = runUnitTest {
        given()
        val fixture = Fixture()
        val error = IllegalStateException("network down")
        everySuspend { fixture.dataSource.getClients(any(), any(), any()) } throws error

        whenn()
        val thrown = assertFailsWith<IllegalStateException> {
            fixture.sut.refresh(ClientsQuery(fixture.businessId))
        }

        then()
        assertEquals(error, thrown)
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveClientsInDb(any()) }
    }

    @Test
    fun `load more adds the page after the cursor for the same search`() = runUnitTest {
        given()
        val fixture = Fixture()
        val page = Page(listOf<Client>(fixture.client()), nextCursor = "after-2")
        fixture.stubPage(search = "anna", cursor = "after-1", page)
        fixture.stubWrites()

        whenn()
        val result = fixture.sut.loadMore(ClientsQuery(fixture.businessId, "anna"), "after-1")

        then()
        assertEquals(page, result)
        verifySuspend { fixture.dataSource.saveClientsInDb(page.items) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.deleteClientsInDb(any()) }
    }

    @Test
    fun `load more restarts from the first page when the cursor is rejected`() = runUnitTest {
        given()
        val fixture = Fixture()
        val firstPage = Page(listOf<Client>(fixture.client()), nextCursor = "after-1")
        everySuspend { fixture.dataSource.getClients(fixture.businessId, null, "stale") } throws
            Error.BusinessError(PageErrorCodes.CURSOR_INVALID, "")
        fixture.stubPage(search = null, cursor = null, firstPage)
        fixture.stubWrites()

        whenn()
        val result = fixture.sut.loadMore(ClientsQuery(fixture.businessId), "stale")

        then()
        assertEquals(firstPage, result)
    }

    @Test
    fun `refreshes the first page of the business clients when the business data fetch runs`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubPage(search = null, cursor = null, Page(emptyList(), nextCursor = null))
            fixture.stubWrites()

            whenn()
            fixture.sut.doOnBusinessDataFetch(fixture.businessId)

            then()
            verifySuspend { fixture.dataSource.getClients(fixture.businessId, null, null) }
        }
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
        appointments = ResourcePermission(),
    ),
)

private fun stubClient(businessId: Uuid) = Client.Detached(
    id = Uuid.random(),
    name = "John",
    lastName = "Doe",
    phone = "123",
    email = "j@e.com",
    businessId = businessId,
)
