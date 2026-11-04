package me.bookk.feature.clients.domain.impl

import dev.mokkery.answering.returns
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
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.ConfirmSensitiveAction
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import me.bookk.feature.clients.domain.api.DeleteClient
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.datasource.ClientsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
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
        val confirmation = mock<ConfirmSensitiveAction>()
        val sut = DeleteClientImpl(dataSource, confirmation)
        init {
            everySuspend { confirmation.invoke(any(), any(), any()) } returns "confirmed-token"
        }
    }

    private fun stubClient() = Client.Detached(
        id = Uuid.random(),
        name = "Jane",
        lastName = "Smith",
        phone = "+380000000001",
        email = "jane@example.com",
        businessId = Uuid.random(),
    )

    @Test
    fun `calls deleteClient with correct businessId and id`() = runUnitTest {
        given()
        val fixture = Fixture()
        val client = stubClient()
        everySuspend {
            fixture.dataSource.deleteClient(client.businessId, client.id, "confirmed-token")
        } returns Unit
        everySuspend { fixture.dataSource.deleteClientInDb(client.id) } returns Unit

        whenn()
        fixture.sut(client)

        then()
        verifySuspend {
            fixture.dataSource.deleteClient(client.businessId, client.id, "confirmed-token")
        }
    }

    @Test
    fun `calls deleteClientInDb`() = runUnitTest {
        given()
        val fixture = Fixture()
        val client = stubClient()
        everySuspend {
            fixture.dataSource.deleteClient(client.businessId, client.id, "confirmed-token")
        } returns Unit
        everySuspend { fixture.dataSource.deleteClientInDb(client.id) } returns Unit

        whenn()
        fixture.sut(client)

        then()
        verifySuspend { fixture.dataSource.deleteClientInDb(client.id) }
    }

    @Test
    fun `requests confirmation for the business and client being deleted`() = runUnitTest {
        given()
        val fixture = Fixture()
        val client = stubClient()
        everySuspend { fixture.dataSource.deleteClient(any(), any(), any()) } returns Unit
        everySuspend { fixture.dataSource.deleteClientInDb(any()) } returns Unit

        whenn()
        fixture.sut(client)

        then()
        verifySuspend {
            fixture.confirmation(SensitiveAction.DELETE_CLIENT, client.businessId, client.id)
        }
    }

    @Test
    fun `failed confirmation neither deletes remotely nor clears the cache`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.confirmation(any(), any(), any()) } throws
            ConfirmSensitiveAction.Error.VerificationFailed()

        whenn()
        val result = runCatching { fixture.sut(stubClient()) }

        then()
        assertFailsWith<DeleteClient.Error.ConfirmationFailed> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) {
            fixture.dataSource.deleteClient(any(), any(), any())
        }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.deleteClientInDb(any()) }
    }

    @Test
    fun `expired deletion token leaves the local client untouched`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.deleteClient(any(), any(), any()) } throws
            me.bookk.core.domain.entity.Error.BusinessError(200040, "expired")

        whenn()
        val result = runCatching { fixture.sut(stubClient()) }

        then()
        assertFailsWith<DeleteClient.Error.ConfirmationRequired> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.deleteClientInDb(any()) }
    }
}
