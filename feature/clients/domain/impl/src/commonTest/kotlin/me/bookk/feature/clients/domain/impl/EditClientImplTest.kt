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
class EditClientImplTest {

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
        val sut = EditClientImpl(dataSource)
    }

    private fun stubClient(businessId: Uuid = Uuid.random()) = Client.Detached(
        id = Uuid.random(),
        name = "John",
        lastName = "Doe",
        phone = "+380000000000",
        email = "john@example.com",
        businessId = businessId,
        description = "VIP client"
    )

    @Test
    fun `returns updated client`() = runUnitTest {
        given()
        val fixture = Fixture()
        val input = stubClient()
        val updated = input.copy(description = "Prefers evening slots")
        everySuspend { fixture.dataSource.updateClient(input) } returns updated
        everySuspend { fixture.dataSource.saveClientsInDb(listOf(updated)) } returns Unit

        whenn()
        val result = fixture.sut(input)

        then()
        assertEquals(updated, result)
    }

    @Test
    fun `saves updated client in DB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val input = stubClient()
        val updated = input.copy(description = "Prefers evening slots")
        everySuspend { fixture.dataSource.updateClient(input) } returns updated
        everySuspend { fixture.dataSource.saveClientsInDb(listOf(updated)) } returns Unit

        whenn()
        fixture.sut(input)

        then()
        verifySuspend { fixture.dataSource.saveClientsInDb(listOf(updated)) }
    }

    @Test
    fun `emits Updated event`() = runUnitTest {
        given()
        val fixture = Fixture()
        val input = stubClient()
        everySuspend { fixture.dataSource.updateClient(input) } returns input
        everySuspend { fixture.dataSource.saveClientsInDb(any()) } returns Unit
        val events = mutableListOf<ClientEvent>()
        val job = launch(Dispatchers.Unconfined) { clientEvents.collect { events.add(it) } }

        whenn()
        fixture.sut(input)

        then()
        job.cancel()
        assertTrue(events.any { it is ClientEvent.Updated })
    }
}
