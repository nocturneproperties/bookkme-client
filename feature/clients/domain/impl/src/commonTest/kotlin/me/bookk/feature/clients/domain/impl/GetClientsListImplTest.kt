package me.bookk.feature.clients.domain.impl

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
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.datasource.ClientsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
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
        val dataSource = mockk<ClientsDataSource>()
        val sut = GetClientsListImpl(dataSource)
    }

    private fun stubClient(businessId: Uuid) = Client.Detached(
        id = Uuid.random(), name = "John", lastName = "Doe",
        phone = "123", email = "j@e.com", businessId = businessId
    )

    @Test
    fun `returns clients from remote`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val clients = listOf(stubClient(businessId))
        coEvery { fixture.dataSource.getClients(businessId) } returns clients
        coJustRun { fixture.dataSource.deleteClientsInDb() }
        coJustRun { fixture.dataSource.saveClientsInDb(clients) }

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(clients, result)
    }

    @Test
    fun `deletes old clients then saves new ones`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val clients = listOf(stubClient(businessId))
        coEvery { fixture.dataSource.getClients(businessId) } returns clients
        coJustRun { fixture.dataSource.deleteClientsInDb() }
        coJustRun { fixture.dataSource.saveClientsInDb(clients) }

        whenn()
        fixture.sut(businessId)

        then()
        coVerify(exactly = 1) { fixture.dataSource.deleteClientsInDb() }
        coVerify(exactly = 1) { fixture.dataSource.saveClientsInDb(clients) }
    }

    @Test
    fun `cached calls onResultAvailable with DB then remote when DB is non-empty`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val cached = listOf(stubClient(businessId))
        val remote = listOf(stubClient(businessId), stubClient(businessId))
        coEvery { fixture.dataSource.getClientsFromDb(businessId) } returns cached
        coEvery { fixture.dataSource.getClients(businessId) } returns remote
        coJustRun { fixture.dataSource.deleteClientsInDb() }
        coJustRun { fixture.dataSource.saveClientsInDb(remote) }
        val received = mutableListOf<List<Client>>()

        whenn()
        fixture.fixture.cached(businessId) { received.add(it) }

        then()
        assertEquals<List<List<Client>>>(listOf(cached, remote), received)
    }

    @Test
    fun `cached skips DB callback when DB is empty`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val remote = listOf(stubClient(businessId))
        coEvery { fixture.dataSource.getClientsFromDb(businessId) } returns emptyList()
        coEvery { fixture.dataSource.getClients(businessId) } returns remote
        coJustRun { fixture.dataSource.deleteClientsInDb() }
        coJustRun { fixture.dataSource.saveClientsInDb(remote) }
        val received = mutableListOf<List<Client>>()

        whenn()
        fixture.fixture.cached(businessId) { received.add(it) }

        then()
        assertEquals<List<List<Client>>>(listOf(remote), received)
    }
}
