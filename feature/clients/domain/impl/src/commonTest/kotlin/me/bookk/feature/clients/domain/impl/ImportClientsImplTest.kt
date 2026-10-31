package me.bookk.feature.clients.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentially
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.domain.entity.Error
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.api.entity.ClientImportEntry
import me.bookk.feature.clients.domain.api.entity.ClientImportResult
import me.bookk.feature.clients.domain.api.entity.RejectedClientImport
import me.bookk.feature.clients.domain.datasource.ClientErrorCodes
import me.bookk.feature.clients.domain.datasource.ClientsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class ImportClientsImplTest {

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
        val dataSource = mock<ClientsDataSource> {
            everySuspend { saveClientsInDb(any()) } returns Unit
        }
        val sut = ImportClientsImpl(dataSource)
    }

    private fun stubEntry(index: Int = 0) = ClientImportEntry(
        name = "John$index",
        lastName = "Doe",
        phone = "+38050000${index.toString().padStart(4, '0')}",
        email = null
    )

    private fun stubCreated(businessId: Uuid, entry: ClientImportEntry) = Client.Detached(
        id = Uuid.random(),
        name = entry.name,
        lastName = entry.lastName,
        phone = entry.phone,
        email = entry.email,
        businessId = businessId
    )

    private fun clientExistsError() = Error.BusinessError(ClientErrorCodes.CLIENT_EXISTS, "Client with this phone or email already exists")

    @Test
    fun `returns created clients and rejected entries`() = runUnitTest {
        given()
        val fixture = Fixture()
        val entries = listOf(stubEntry(0), stubEntry(1))
        val created = stubCreated(fixture.businessId, entries[0])
        val rejected = RejectedClientImport(index = 1, reason = RejectedClientImport.Reason.ALREADY_EXISTS)
        everySuspend { fixture.dataSource.importClients(fixture.businessId, entries) } returns
            ClientImportResult(created = listOf(created), rejected = listOf(rejected))

        whenn()
        val result = fixture.sut(fixture.businessId, entries)

        then()
        assertEquals(ClientImportResult(created = listOf(created), rejected = listOf(rejected)), result)
    }

    @Test
    fun `saves created clients in DB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val entries = listOf(stubEntry(0))
        val created = stubCreated(fixture.businessId, entries[0])
        everySuspend { fixture.dataSource.importClients(fixture.businessId, entries) } returns
            ClientImportResult(created = listOf(created), rejected = emptyList())

        whenn()
        fixture.sut(fixture.businessId, entries)

        then()
        verifySuspend { fixture.dataSource.saveClientsInDb(listOf(created)) }
    }

    @Test
    fun `sends trimmed entry fields to backend`() = runUnitTest {
        given()
        val fixture = Fixture()
        val input = ClientImportEntry(
            name = "  John ",
            lastName = " Doe\n",
            phone = " +380501234567  ",
            email = "john@example.com ",
            description = "  Prefers mornings "
        )
        val expected = ClientImportEntry(
            name = "John",
            lastName = "Doe",
            phone = "+380501234567",
            email = "john@example.com",
            description = "Prefers mornings"
        )
        everySuspend { fixture.dataSource.importClients(any(), any()) } returns
            ClientImportResult(created = emptyList(), rejected = emptyList())

        whenn()
        fixture.sut(fixture.businessId, listOf(input))

        then()
        verifySuspend { fixture.dataSource.importClients(fixture.businessId, listOf(expected)) }
    }

    @Test
    fun `splits more than 500 entries into requests of at most 500`() = runUnitTest {
        given()
        val fixture = Fixture()
        val entries = (0 until 1001).map(::stubEntry)
        everySuspend { fixture.dataSource.importClients(any(), any()) } returns
            ClientImportResult(created = emptyList(), rejected = emptyList())

        whenn()
        fixture.sut(fixture.businessId, entries)

        then()
        verifySuspend(VerifyMode.order) {
            fixture.dataSource.importClients(fixture.businessId, entries.subList(0, 500))
            fixture.dataSource.importClients(fixture.businessId, entries.subList(500, 1000))
            fixture.dataSource.importClients(fixture.businessId, entries.subList(1000, 1001))
        }
    }

    @Test
    fun `reports rejected positions relative to the whole import`() = runUnitTest {
        given()
        val fixture = Fixture()
        val entries = (0 until 502).map(::stubEntry)
        everySuspend { fixture.dataSource.importClients(fixture.businessId, entries.subList(0, 500)) } returns
            ClientImportResult(
                created = emptyList(),
                rejected = listOf(RejectedClientImport(3, RejectedClientImport.Reason.INVALID_FIELDS))
            )
        everySuspend { fixture.dataSource.importClients(fixture.businessId, entries.subList(500, 502)) } returns
            ClientImportResult(
                created = emptyList(),
                rejected = listOf(RejectedClientImport(1, RejectedClientImport.Reason.MISSING_CONTACT_INFO))
            )

        whenn()
        val result = fixture.sut(fixture.businessId, entries)

        then()
        assertEquals(
            listOf(
                RejectedClientImport(3, RejectedClientImport.Reason.INVALID_FIELDS),
                RejectedClientImport(501, RejectedClientImport.Reason.MISSING_CONTACT_INFO)
            ),
            result.rejected
        )
    }

    @Test
    fun `retries a request once when a concurrent write took one of its contacts`() = runUnitTest {
        given()
        val fixture = Fixture()
        val entries = listOf(stubEntry(0))
        val retried = ClientImportResult(
            created = emptyList(),
            rejected = listOf(RejectedClientImport(0, RejectedClientImport.Reason.ALREADY_EXISTS))
        )
        everySuspend { fixture.dataSource.importClients(fixture.businessId, entries) } sequentially {
            throws(clientExistsError())
            returns(retried)
        }

        whenn()
        val result = fixture.sut(fixture.businessId, entries)

        then()
        assertEquals(retried, result)
    }

    @Test
    fun `rethrows when the retried request fails again`() = runUnitTest {
        given()
        val fixture = Fixture()
        val entries = listOf(stubEntry(0))
        everySuspend { fixture.dataSource.importClients(fixture.businessId, entries) } throws clientExistsError()

        whenn()
        val error = assertFailsWith<Error.BusinessError> { fixture.sut(fixture.businessId, entries) }

        then()
        assertEquals(ClientErrorCodes.CLIENT_EXISTS, error.errorCode)
        verifySuspend(VerifyMode.exactly(2)) { fixture.dataSource.importClients(fixture.businessId, entries) }
    }

    @Test
    fun `does not retry a request that fails with another error`() = runUnitTest {
        given()
        val fixture = Fixture()
        val entries = listOf(stubEntry(0))
        val failure = Error.NoConnectionError(Exception())
        everySuspend { fixture.dataSource.importClients(fixture.businessId, entries) } throws failure

        whenn()
        val error = assertFailsWith<Error.NoConnectionError> { fixture.sut(fixture.businessId, entries) }

        then()
        assertSame(failure, error)
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.importClients(fixture.businessId, entries) }
    }

    @Test
    fun `makes no request for an empty import`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut(fixture.businessId, emptyList())

        then()
        assertEquals(ClientImportResult(created = emptyList(), rejected = emptyList()), result)
        verifySuspend(VerifyMode.not) { fixture.dataSource.importClients(any(), any()) }
    }
}
