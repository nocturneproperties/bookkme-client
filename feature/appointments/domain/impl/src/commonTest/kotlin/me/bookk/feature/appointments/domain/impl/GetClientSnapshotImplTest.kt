package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.clients.domain.api.GetClient
import me.bookk.feature.clients.domain.api.entity.Client
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

class GetClientSnapshotImplTest {

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
        val getClient = mock<GetClient>()
        val sut = GetClientSnapshotImpl(getClient)
    }

    @Test
    fun `returns a snapshot of the cached client`() = runUnitTest {
        given()
        val fixture = Fixture()
        val client = Client.Detached(
            id = Uuid.random(),
            name = "Anna",
            lastName = "Kowalska",
            phone = "+48501234567",
            email = "anna@example.com",
            businessId = Uuid.random(),
        )
        everySuspend { fixture.getClient(client.id) } returns client

        whenn()
        val snapshot = fixture.sut(client.id)

        then()
        assertEquals(
            ClientSnapshot(
                id = client.id,
                fullName = "Anna Kowalska",
                phone = "+48501234567",
                email = "anna@example.com",
            ),
            snapshot,
        )
    }

    @Test
    fun `propagates the error when the client is not cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        everySuspend { fixture.getClient(id) } throws IllegalStateException("missing")

        whenn()
        val error = assertFailsWith<IllegalStateException> { fixture.sut(id) }

        then()
        assertEquals("missing", error.message)
    }
}
