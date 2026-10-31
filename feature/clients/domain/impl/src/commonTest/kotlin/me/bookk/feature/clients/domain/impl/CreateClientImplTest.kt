package me.bookk.feature.clients.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
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
        val fixture = Fixture()
        val input = stubClient()
        val created = input.copy(id = Uuid.random())
        everySuspend { fixture.dataSource.createClient(input) } returns created
        everySuspend { fixture.dataSource.saveClientsInDb(listOf(created)) } returns Unit

        whenn()
        val result = fixture.sut(input)

        then()
        assertEquals(created, result)
    }

    @Test
    fun `saves created client in DB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val input = stubClient()
        val created = input.copy(id = Uuid.random())
        everySuspend { fixture.dataSource.createClient(input) } returns created
        everySuspend { fixture.dataSource.saveClientsInDb(listOf(created)) } returns Unit

        whenn()
        fixture.sut(input)

        then()
        verifySuspend { fixture.dataSource.saveClientsInDb(listOf(created)) }
    }

    @Test
    fun `sends trimmed client fields to backend`() = runUnitTest {
        given()
        val fixture = Fixture()
        val trimmed = stubClient()
        val input = trimmed.copy(
            name = "  John ",
            lastName = " Doe\n",
            phone = " +380000000000  ",
            email = "john@example.com ",
            description = "  Prefers mornings "
        )
        val expected = trimmed.copy(description = "Prefers mornings")
        everySuspend { fixture.dataSource.createClient(expected) } returns expected
        everySuspend { fixture.dataSource.saveClientsInDb(listOf(expected)) } returns Unit

        whenn()
        fixture.sut(input)

        then()
        verifySuspend { fixture.dataSource.createClient(expected) }
    }

    @Test
    fun `sends trimmed integrated client fields to backend`() = runUnitTest {
        given()
        val fixture = Fixture()
        val trimmed = Client.Integrated(
            id = Uuid.random(),
            name = "John",
            lastName = "Doe",
            phone = "+380000000000",
            email = "john@example.com",
            businessId = Uuid.random(),
            userId = Uuid.random(),
            description = "VIP client"
        )
        val input = trimmed.copy(
            name = " John ",
            lastName = "Doe ",
            phone = " +380000000000",
            email = " john@example.com\n",
            description = " VIP client "
        )
        everySuspend { fixture.dataSource.createClient(trimmed) } returns trimmed
        everySuspend { fixture.dataSource.saveClientsInDb(listOf(trimmed)) } returns Unit

        whenn()
        fixture.sut(input)

        then()
        verifySuspend { fixture.dataSource.createClient(trimmed) }
    }
}
