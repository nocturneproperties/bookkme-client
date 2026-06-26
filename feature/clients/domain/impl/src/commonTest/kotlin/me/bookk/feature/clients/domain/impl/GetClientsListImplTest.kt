package me.bookk.feature.clients.domain.impl

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
        val dataSource = mock<ClientsDataSource>()
        val sut = GetClientsListImpl(dataSource)
    }

    private fun stubClient(businessId: Uuid) = Client.Detached(
        id = Uuid.random(), name = "John", lastName = "Doe",
        phone = "123", email = "j@e.com", businessId = businessId
    )

    @Test
    fun `returns clients from remote`() = runUnitTest {
        given()
        val sut = Fixture()
        val businessId = Uuid.random()
        val clients = listOf(stubClient(businessId))
        everySuspend { sut.dataSource.getClients(businessId) } returns clients
        everySuspend { sut.dataSource.deleteClientsInDb() } returns Unit
        everySuspend { sut.dataSource.saveClientsInDb(clients) } returns Unit

        whenn()
        val result = sut.sut(businessId)

        then()
        assertEquals(clients, result)
    }

    @Test
    fun `deletes old clients then saves new ones`() = runUnitTest {
        given()
        val sut = Fixture()
        val businessId = Uuid.random()
        val clients = listOf(stubClient(businessId))
        everySuspend { sut.dataSource.getClients(businessId) } returns clients
        everySuspend { sut.dataSource.deleteClientsInDb() } returns Unit
        everySuspend { sut.dataSource.saveClientsInDb(clients) } returns Unit

        whenn()
        sut.sut(businessId)

        then()
        verifySuspend(VerifyMode.exactly(1)) { sut.dataSource.deleteClientsInDb() }
        verifySuspend(VerifyMode.exactly(1)) { sut.dataSource.saveClientsInDb(clients) }
    }

    @Test
    fun `cached calls onResultAvailable with DB then remote when DB is non-empty`() = runUnitTest {
        given()
        val sut = Fixture()
        val businessId = Uuid.random()
        val cached = listOf(stubClient(businessId))
        val remote = listOf(stubClient(businessId), stubClient(businessId))
        everySuspend { sut.dataSource.getClientsFromDb(businessId) } returns cached
        everySuspend { sut.dataSource.getClients(businessId) } returns remote
        everySuspend { sut.dataSource.deleteClientsInDb() } returns Unit
        everySuspend { sut.dataSource.saveClientsInDb(remote) } returns Unit
        val received = mutableListOf<List<Client>>()

        whenn()
        sut.sut.cached(businessId) { received.add(it) }

        then()
        assertEquals<List<List<Client>>>(listOf(cached, remote), received)
    }

    @Test
    fun `cached skips DB callback when DB is empty`() = runUnitTest {
        given()
        val sut = Fixture()
        val businessId = Uuid.random()
        val remote = listOf(stubClient(businessId))
        everySuspend { sut.dataSource.getClientsFromDb(businessId) } returns emptyList()
        everySuspend { sut.dataSource.getClients(businessId) } returns remote
        everySuspend { sut.dataSource.deleteClientsInDb() } returns Unit
        everySuspend { sut.dataSource.saveClientsInDb(remote) } returns Unit
        val received = mutableListOf<List<Client>>()

        whenn()
        sut.sut.cached(businessId) { received.add(it) }

        then()
        assertEquals<List<List<Client>>>(listOf(remote), received)
    }
}
