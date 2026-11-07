package me.bookk.feature.appointments.data.datasource

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.protobuf.protobuf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import me.bookk.core.data.BusinessServerError
import me.bookk.core.data.dataSerializer
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.dao.AppointmentDao
import me.bookk.feature.appointments.data.mapping.toRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentRemote
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import me.bookk.core.domain.entity.Error as DomainError

class AppointmentDetailsHttpTest {

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
        val expected = Appointment.stub(date = LocalDateTime(2026, 11, 5, 10, 0))
            .copy(status = AppointmentStatus.UNRESOLVED)
        val preferences = mock<Preferences>()
        val provider = mock<PreferenceProvider> {
            every { get(any()) } returns preferences
        }
        val dao = mock<AppointmentDao>()
        var request: HttpRequestData? = null
        var status = HttpStatusCode.OK
        var contentType = ContentType.Application.ProtoBuf
        var body = dataSerializer.encodeToByteArray(
            AppointmentRemote.serializer(),
            expected.toRemote(),
        )
        val client = HttpClient(
            MockEngine {
                request = it
                respond(body, status, headersOf(HttpHeaders.ContentType, contentType.toString()))
            },
        ) {
            expectSuccess = true
            install(Resources)
            install(ContentNegotiation) { protobuf(dataSerializer) }
        }
        val sut = CommonAppointmentDataSource(client, dao, provider)
    }

    @Test
    fun `fetches exact appointment by GET without touching the cache`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = try {
            fixture.sut.getAppointmentFromRemote(fixture.expected.id)
        } finally {
            fixture.client.close()
        }

        then()
        assertEquals(HttpMethod.Get, fixture.request?.method)
        assertEquals("/api/appointments/${fixture.expected.id}", fixture.request?.url?.encodedPath)
        assertEquals(fixture.expected, result)
    }

    @Test
    fun `maps plain text not found response without decoding it as an appointment`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.status = HttpStatusCode.NotFound
        fixture.contentType = ContentType.Text.Plain
        fixture.body = "Not found".encodeToByteArray()

        whenn()
        val result = try {
            runCatching { fixture.sut.getAppointmentFromRemote(fixture.expected.id) }
        } finally {
            fixture.client.close()
        }

        then()
        assertFailsWith<DomainError.NotFound> { result.getOrThrow() }
    }

    @Test
    fun `maps suspended employee response through the shared access handler`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.status = HttpStatusCode.Forbidden
        fixture.body = dataSerializer.encodeToByteArray(
            BusinessServerError.serializer(),
            BusinessServerError(200034, "Suspended"),
        )

        whenn()
        val result = try {
            runCatching { fixture.sut.getAppointmentFromRemote(fixture.expected.id) }
        } finally {
            fixture.client.close()
        }

        then()
        assertFailsWith<DomainError.BusinessAccessSuspended> { result.getOrThrow() }
    }
}
