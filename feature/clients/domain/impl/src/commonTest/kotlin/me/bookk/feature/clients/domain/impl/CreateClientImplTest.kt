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
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class CreateClientImplTest {

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
        val sut = CreateClientImpl(dataSource)
    }

    private fun stubClient(businessId: Uuid = Uuid.random()) = Client.Detached(
        id = Uuid.random(),
        name = "John",
        lastName = "Doe",
        phone = "+380000000000",
        email = "john@example.com",
        businessId = businessId
    )

    @Test
    fun `returns created client`() = runUnitTest {
        given()
        val sut = Fixture()
        val input = stubClient()
        val created = input.copy(id = Uuid.random())
        everySuspend { sut.dataSource.createClient(input) } returns created
        everySuspend { sut.dataSource.saveClientsInDb(listOf(created)) } returns Unit

        whenn()
        val result = sut.sut(input)

        then()
        assertEquals(created, result)
    }

    @Test
    fun `saves created client in DB`() = runUnitTest {
        given()
        val sut = Fixture()
        val input = stubClient()
        val created = input.copy(id = Uuid.random())
        everySuspend { sut.dataSource.createClient(input) } returns created
        everySuspend { sut.dataSource.saveClientsInDb(listOf(created)) } returns Unit

        whenn()
        sut.sut(input)

        then()
        verifySuspend { sut.dataSource.saveClientsInDb(listOf(created)) }
    }

    @Test
    fun `emits Created event`() = runUnitTest {
        given()
        val sut = Fixture()
        val input = stubClient()
        everySuspend { sut.dataSource.createClient(input) } returns input
        everySuspend { sut.dataSource.saveClientsInDb(any()) } returns Unit
        val events = mutableListOf<ClientEvent>()
        val job = launch(Dispatchers.Unconfined) { clientEvents.collect { events.add(it) } }

        whenn()
        sut.sut(input)

        then()
        job.cancel()
        assertTrue(events.any { it is ClientEvent.Created })
    }
}
