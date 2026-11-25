package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import me.bookk.core.domain.entity.Error
import me.bookk.core.domain.pagination.Page
import me.bookk.core.domain.pagination.PageErrorCodes
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentHistoryQuery
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

class GetAppointmentHistoryImplTest {

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
        val dataSource = mock<AppointmentDataSource>()
        val sut = GetAppointmentHistoryImpl(dataSource)
        val query = AppointmentHistoryQuery(businessId)

        fun stubCached(vararg appointments: Appointment) {
            every { dataSource.observeAppointmentsDBChanges(businessId) } returns
                flowOf(appointments.toList())
        }

        fun stubPage(search: String?, cursor: String?, page: Page<Appointment>) {
            everySuspend { dataSource.getAppointmentHistory(businessId, search, cursor) } returns
                page
        }

        fun stubWrites(cachedIds: List<Uuid> = emptyList()) {
            everySuspend { dataSource.getAppointmentIdsInDb(any(), any(), any()) } returns cachedIds
            everySuspend { dataSource.deleteAppointmentsInDb(any()) } returns Unit
            everySuspend { dataSource.saveAppointmentsInDB(any()) } returns Unit
        }

        fun appointment(
            day: Int = 1,
            clientName: String = "Anna Kowalska",
            serviceName: String = "Haircut",
        ) = Appointment.stub(businessId = businessId, date = LocalDateTime(2026, 11, day, 10, 0))
            .copy(
                client = ClientSnapshot.stub().copy(fullName = clientName),
                services = listOf(ServiceSnapshot.stub().copy(name = serviceName)),
            )
    }

    @Test
    fun `flow without a search emits every cached appointment of the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointments = arrayOf(fixture.appointment(day = 2), fixture.appointment(day = 1))
        fixture.stubCached(*appointments)

        whenn()
        val result = fixture.sut.flow(fixture.businessId, search = " ").first()

        then()
        assertEquals(appointments.toList(), result)
    }

    @Test
    fun `flow with a search keeps appointments whose client name contains it`() = runUnitTest {
        given()
        val fixture = Fixture()
        val anna = fixture.appointment(clientName = "Anna Kowalska")
        fixture.stubCached(anna, fixture.appointment(clientName = "Olga Nowak"))

        whenn()
        val result = fixture.sut.flow(fixture.businessId, search = "KOWAL").first()

        then()
        assertEquals(listOf(anna), result)
    }

    @Test
    fun `flow with a search keeps appointments with a service whose name contains it`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val coloring = fixture.appointment(serviceName = "Hair coloring")
            fixture.stubCached(coloring, fixture.appointment(serviceName = "Manicure"))

            whenn()
            val result = fixture.sut.flow(fixture.businessId, search = "color").first()

            then()
            assertEquals(listOf(coloring), result)
        }

    @Test
    fun `refresh saves the first page and returns it`() = runUnitTest {
        given()
        val fixture = Fixture()
        val page = Page(listOf(fixture.appointment(day = 3)), nextCursor = "after-3")
        fixture.stubPage(search = null, cursor = null, page)
        fixture.stubWrites()

        whenn()
        val result = fixture.sut.refresh(fixture.query)

        then()
        assertEquals(page, result)
        verifySuspend { fixture.dataSource.saveAppointmentsInDB(page.items) }
    }

    @Test
    fun `refresh removes cached appointments from the newest down to the first page's oldest`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val newest = fixture.appointment(day = 5)
            val oldest = fixture.appointment(day = 3)
            val staleId = Uuid.random()
            fixture.stubPage(
                search = null,
                cursor = null,
                Page(listOf(newest, oldest), nextCursor = "after-3"),
            )
            fixture.stubWrites(cachedIds = listOf(newest.id, staleId))

            whenn()
            fixture.sut.refresh(fixture.query)

            then()
            verifySuspend {
                fixture.dataSource.getAppointmentIdsInDb(fixture.businessId, oldest.date, null)
            }
            verifySuspend { fixture.dataSource.deleteAppointmentsInDb(listOf(staleId)) }
        }

    @Test
    fun `refresh of a history that fits one page removes every other cached appointment`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubPage(
                search = null,
                cursor = null,
                Page(listOf(fixture.appointment()), nextCursor = null),
            )
            fixture.stubWrites()

            whenn()
            fixture.sut.refresh(fixture.query)

            then()
            verifySuspend {
                fixture.dataSource.getAppointmentIdsInDb(fixture.businessId, null, null)
            }
        }

    @Test
    fun `refresh of an empty history removes every cached appointment`() = runUnitTest {
        given()
        val fixture = Fixture()
        val staleId = Uuid.random()
        fixture.stubPage(search = null, cursor = null, Page(emptyList(), nextCursor = null))
        fixture.stubWrites(cachedIds = listOf(staleId))

        whenn()
        fixture.sut.refresh(fixture.query)

        then()
        verifySuspend { fixture.dataSource.deleteAppointmentsInDb(listOf(staleId)) }
    }

    @Test
    fun `refresh with a search adds the found appointments without removing others`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val found = fixture.appointment()
            val page = Page(listOf(found), nextCursor = null)
            fixture.stubPage(search = "anna", cursor = null, page)
            fixture.stubWrites()

            whenn()
            fixture.sut.refresh(AppointmentHistoryQuery(fixture.businessId, search = "anna"))

            then()
            verifySuspend { fixture.dataSource.saveAppointmentsInDB(page.items) }
            verifySuspend(VerifyMode.exactly(0)) {
                fixture.dataSource.getAppointmentIdsInDb(any(), any(), any())
            }
        }

    @Test
    fun `refresh propagates a fetch error and keeps the cache`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getAppointmentHistory(any(), any(), any()) } throws
            IllegalStateException("offline")

        whenn()
        assertFailsWith<IllegalStateException> { fixture.sut.refresh(fixture.query) }

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveAppointmentsInDB(any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.deleteAppointmentsInDb(any()) }
    }

    @Test
    fun `load more saves the page after the cursor`() = runUnitTest {
        given()
        val fixture = Fixture()
        val page = Page(listOf(fixture.appointment(day = 2)), nextCursor = "after-2")
        fixture.stubPage(search = null, cursor = "after-3", page)
        fixture.stubWrites()

        whenn()
        val result = fixture.sut.loadMore(fixture.query, "after-3")

        then()
        assertEquals(page, result)
        verifySuspend { fixture.dataSource.saveAppointmentsInDB(page.items) }
    }

    @Test
    fun `load more removes cached appointments within the page's own dates`() = runUnitTest {
        given()
        val fixture = Fixture()
        val first = fixture.appointment(day = 2)
        val last = fixture.appointment(day = 1)
        fixture.stubPage(
            search = null,
            cursor = "after-3",
            Page(listOf(first, last), nextCursor = "after-1"),
        )
        fixture.stubWrites()

        whenn()
        fixture.sut.loadMore(fixture.query, "after-3")

        then()
        verifySuspend {
            fixture.dataSource.getAppointmentIdsInDb(fixture.businessId, last.date, first.date)
        }
    }

    @Test
    fun `load more of the last page removes cached appointments older than it`() = runUnitTest {
        given()
        val fixture = Fixture()
        val first = fixture.appointment(day = 2)
        fixture.stubPage(search = null, cursor = "after-3", Page(listOf(first), nextCursor = null))
        fixture.stubWrites()

        whenn()
        fixture.sut.loadMore(fixture.query, "after-3")

        then()
        verifySuspend {
            fixture.dataSource.getAppointmentIdsInDb(fixture.businessId, null, first.date)
        }
    }

    @Test
    fun `load more of an empty page removes nothing`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubPage(search = null, cursor = "after-3", Page(emptyList(), nextCursor = null))
        fixture.stubWrites()

        whenn()
        fixture.sut.loadMore(fixture.query, "after-3")

        then()
        verifySuspend(VerifyMode.exactly(0)) {
            fixture.dataSource.getAppointmentIdsInDb(any(), any(), any())
        }
    }

    @Test
    fun `load more restarts from the first page when the cursor is rejected`() = runUnitTest {
        given()
        val fixture = Fixture()
        val firstPage = Page(listOf(fixture.appointment()), nextCursor = "after-1")
        everySuspend {
            fixture.dataSource.getAppointmentHistory(fixture.businessId, null, "stale")
        } throws Error.BusinessError(PageErrorCodes.CURSOR_INVALID, "")
        fixture.stubPage(search = null, cursor = null, firstPage)
        fixture.stubWrites()

        whenn()
        val result = fixture.sut.loadMore(fixture.query, "stale")

        then()
        assertEquals(firstPage, result)
    }
}
