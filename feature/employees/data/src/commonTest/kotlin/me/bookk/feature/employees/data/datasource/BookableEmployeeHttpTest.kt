package me.bookk.feature.employees.data.datasource

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
import kotlinx.serialization.builtins.ListSerializer
import library.money.api.Money
import me.bookk.core.data.dataSerializer
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.employees.data.remote.model.BookableEmployeeRemote
import me.bookk.feature.employees.data.remote.model.ScheduleRemote
import me.bookk.feature.employees.data.remote.model.ServiceRemote
import me.bookk.feature.employees.domain.api.entity.BookableEmployee
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlin.uuid.Uuid

class BookableEmployeeHttpTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val businessId = Uuid.random()
    private val haircut = Service(
        id = Uuid.random(),
        businessId = businessId,
        group = ServiceGroup(Uuid.random(), businessId, "Hair", Instant.fromEpochMilliseconds(0)),
        name = "Haircut",
        duration = 45.minutes,
        price = Money(60_000, Money.SupportedCurrency.UAH),
        isAvailable = true,
        createdAt = Instant.fromEpochMilliseconds(0),
    )
    private val andrii = BookableEmployee(
        id = Uuid.random(),
        businessId = businessId,
        name = "Andrii",
        lastName = "Koval",
        services = listOf(haircut),
        schedule = WorkingSchedule(),
    )

    private class Fixture(
        employees: List<BookableEmployee>,
    ) {
        var request: HttpRequestData? = null
        val client = HttpClient(
            MockEngine {
                request = it
                respond(
                    dataSerializer.encodeToByteArray(
                        ListSerializer(BookableEmployeeRemote.serializer()),
                        employees.map { it.toRemote() },
                    ),
                    HttpStatusCode.OK,
                    headersOf(HttpHeaders.ContentType, ContentType.Application.ProtoBuf.toString()),
                )
            },
        ) {
            expectSuccess = true
            install(Resources)
            install(ContentNegotiation) { protobuf(dataSerializer) }
        }
        val sut = CommonBookableEmployeeDataSource(client, FakeBookableEmployeeDao())

        private fun BookableEmployee.toRemote(): BookableEmployeeRemote {
            return BookableEmployeeRemote(
                id = id,
                businessId = businessId,
                name = name,
                lastName = lastName,
                services = services.map { ServiceRemote.fromDomain(it) },
                schedule = ScheduleRemote.fromDomain(schedule),
            )
        }
    }

    @Test
    fun `asks the business for the employees clients can book`() = runUnitTest {
        given()
        val fixture = Fixture(listOf(andrii))

        whenn()
        fixture.sut.getBookableEmployees(businessId)
        fixture.client.close()

        then()
        assertEquals(HttpMethod.Get, fixture.request?.method)
        assertEquals(
            "/api/business/$businessId/employee/bookable",
            fixture.request?.url?.encodedPath,
        )
    }

    @Test
    fun `reads the bookable employees with the services they provide`() = runUnitTest {
        given()
        val fixture = Fixture(listOf(andrii))

        whenn()
        val employees = fixture.sut.getBookableEmployees(businessId)
        fixture.client.close()

        then()
        assertEquals(listOf(andrii), employees)
    }
}
