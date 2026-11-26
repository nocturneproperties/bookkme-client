package me.bookk.feature.authorization.data.datasource

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.protobuf.protobuf
import kotlinx.coroutines.flow.first
import library.cache.api.PreferenceProvider
import me.bookk.core.data.dataSerializer
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.data.remote.model.AccountDeletionStatusRemote
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

class CommonAccountDeletionDataSourceTest {
    private class Fixture(
        status: AccountDeletionStatusRemote = AccountDeletionStatusRemote(),
    ) {
        val preferences = FakePreferences()
        val provider = mock<PreferenceProvider> {
            every { get("account_deletion_prefs") } returns preferences
        }
        var method: HttpMethod? = null
        var path: String? = null
        val client = HttpClient(
            MockEngine {
                method = it.method
                path = it.url.encodedPath
                respond(
                    dataSerializer.encodeToByteArray(
                        AccountDeletionStatusRemote.serializer(),
                        status,
                    ),
                    HttpStatusCode.OK,
                    headersOf(HttpHeaders.ContentType, ContentType.Application.ProtoBuf.toString()),
                )
            },
        ) {
            install(Resources)
            install(ContentNegotiation) { protobuf(dataSerializer) }
            defaultRequest { contentType(ContentType.Application.ProtoBuf) }
        }
        val sut = CommonAccountDeletionDataSource(client, provider)
    }

    @Test
    fun `reads the scheduled deletion from the deletion route`() = runUnitTest {
        given()
        val fixture = Fixture(AccountDeletionStatusRemote(DELETES_AT))

        whenn()
        val deletesAt = fixture.sut.getScheduledDeletion()

        then()
        assertEquals(DELETES_AT, deletesAt)
        assertEquals(HttpMethod.Get, fixture.method)
        assertEquals("/api/auth/account/deletion", fixture.path)
    }

    @Test
    fun `reads no deletion when none is pending`() = runUnitTest {
        given()
        val fixture = Fixture(AccountDeletionStatusRemote(deletesAt = null))

        whenn()
        val deletesAt = fixture.sut.getScheduledDeletion()

        then()
        assertNull(deletesAt)
    }

    @Test
    fun `remembers the scheduled deletion`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.saveScheduledDeletion(DELETES_AT)

        then()
        assertEquals(DELETES_AT, fixture.sut.observeScheduledDeletion().first())
    }

    @Test
    fun `forgets a deletion that is no longer pending`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.saveScheduledDeletion(DELETES_AT)

        whenn()
        fixture.sut.saveScheduledDeletion(null)

        then()
        assertNull(fixture.sut.observeScheduledDeletion().first())
    }

    @Test
    fun `forgets the scheduled deletion on logout`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.saveScheduledDeletion(DELETES_AT)

        whenn()
        fixture.sut.doOnLogOut()

        then()
        assertNull(fixture.sut.observeScheduledDeletion().first())
    }

    private companion object {
        val DELETES_AT = Instant.parse("2026-12-03T14:00:00Z")
    }
}
