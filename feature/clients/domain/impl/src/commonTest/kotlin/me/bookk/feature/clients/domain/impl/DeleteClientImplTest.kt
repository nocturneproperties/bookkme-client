package me.bookk.feature.clients.domain.impl

import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
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
        val dataSource = mockk<ClientsDataSource>()
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
        val fixture = Fixture()
        val client = stubClient()
        coJustRun { fixture.dataSource.deleteClient(client.businessId, client.id) }
        coJustRun { fixture.dataSource.deleteClientInDb(client.id) }

        whenn()
        fixture.sut(client)

        then()
        coVerify { fixture.dataSource.deleteClient(client.businessId, client.id) }
    }

    @Test
    fun `calls deleteClientInDb`() = runUnitTest {
        given()
        val fixture = Fixture()
        val client = stubClient()
        coJustRun { fixture.dataSource.deleteClient(client.businessId, client.id) }
        coJustRun { fixture.dataSource.deleteClientInDb(client.id) }

        whenn()
        fixture.sut(client)

        then()
        coVerify { fixture.dataSource.deleteClientInDb(client.id) }
    }

    @Test
    fun `emits Deleted event`() = runUnitTest {
        given()
        val fixture = Fixture()
        val client = stubClient()
        coJustRun { fixture.dataSource.deleteClient(any(), any()) }
        coJustRun { fixture.dataSource.deleteClientInDb(any()) }
        val events = mutableListOf<ClientEvent>()
        val job = launch(Dispatchers.Unconfined) { clientEvents.collect { events.add(it) } }

        whenn()
        fixture.sut(client)

        then()
        job.cancel()
        assertTrue(events.any { it is ClientEvent.Deleted })
    }
}
