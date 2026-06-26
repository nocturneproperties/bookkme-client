package me.bookk.feature.clients.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
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
class GetClientImplTest {

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
        val sut = GetClientImpl(dataSource)
    }

    @Test
    fun `returns client by id`() = runUnitTest {
        given()
        val sut = Fixture()
        val id = Uuid.random()
        val expected = Client.Detached(
            id = id,
            name = "John",
            lastName = "Doe",
            phone = "123",
            email = "john@example.com",
            businessId = Uuid.random()
        )
        everySuspend { sut.dataSource.getClient(id) } returns expected

        whenn()
        val result = sut.sut(id)

        then()
        assertEquals(expected, result)
    }
}
