package me.bookk.feature.clients.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.api.entity.ClientEvent
import me.bookk.feature.clients.domain.api.entity.clientEvents
import me.bookk.feature.clients.domain.datasource.ClientsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteClientImplTest {

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
        val sut = DeleteClientImpl(dataSource)
    }

    private fun stubClient() = Client.Detached(
        id = Uuid.random(),
        name = "Jane",
        lastName = "Smith",
        phone = "+380000000001",
        email = "jane@example.com",
        businessId = Uuid.random()
    )

    @Test
    fun `calls deleteClient with correct businessId and id`() = runUnitTest {
        given()
        val sut = Fixture()
        val client = stubClient()
        everySuspend { sut.dataSource.deleteClient(client.businessId, client.id) } returns Unit
        everySuspend { sut.dataSource.deleteClientInDb(client.id) } returns Unit

        whenn()
        sut.sut(client)

        then()
        verifySuspend { sut.dataSource.deleteClient(client.businessId, client.id) }
    }

    @Test
    fun `calls deleteClientInDb`() = runUnitTest {
        given()
        val sut = Fixture()
        val client = stubClient()
        everySuspend { sut.dataSource.deleteClient(client.businessId, client.id) } returns Unit
        everySuspend { sut.dataSource.deleteClientInDb(client.id) } returns Unit

        whenn()
        sut.sut(client)

        then()
        verifySuspend { sut.dataSource.deleteClientInDb(client.id) }
    }

    @Test
    fun `emits Deleted event`() = runUnitTest {
        given()
        val sut = Fixture()
        val client = stubClient()
        everySuspend { sut.dataSource.deleteClient(any(), any()) } returns Unit
        everySuspend { sut.dataSource.deleteClientInDb(any()) } returns Unit
        val events = mutableListOf<ClientEvent>()
        val job = launch(Dispatchers.Unconfined) { clientEvents.collect { events.add(it) } }

        whenn()
        sut.sut(client)

        then()
        job.cancel()
        assertTrue(events.any { it is ClientEvent.Deleted })
    }
}
