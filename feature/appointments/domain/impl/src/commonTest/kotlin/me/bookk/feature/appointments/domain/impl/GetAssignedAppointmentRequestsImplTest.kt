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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.domain.entity.Error
import me.bookk.core.domain.pagination.Page
import me.bookk.core.domain.pagination.PageErrorCodes
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestStatus
import me.bookk.feature.appointments.domain.datasource.AppointmentRequestDataSource
import me.bookk.feature.business.domain.api.business.ObserveUserBusinessesChanges
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetAssignedAppointmentRequestsImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class Fixture(
        val employeeId: Uuid? = Uuid.random(),
    ) {
        val businessId = Uuid.random()
        val dataSource = mock<AppointmentRequestDataSource>()
        val businesses = mock<ObserveUserBusinessesChanges> {
            every { invoke() } returns flowOf(listOf(stubBusiness(businessId, employeeId)))
        }
        val sut = GetAssignedAppointmentRequestsImpl(dataSource, businesses)

        fun stubPage(cursor: String?, page: Page<AppointmentRequest>) {
            everySuspend {
                dataSource.getAssignedAppointmentRequests(businessId, employeeId!!, cursor)
            } returns page
        }

        fun stubCachedIds(ids: List<Uuid>) {
            everySuspend { dataSource.getAppointmentRequestIdsInDb(businessId) } returns ids
        }

        fun stubWrites() {
            everySuspend { dataSource.deleteAppointmentRequestsInDb(any()) } returns Unit
            everySuspend { dataSource.saveAppointmentRequestsInDB(any()) } returns Unit
            everySuspend { dataSource.saveLastSyncedAt(any()) } returns Unit
        }

        fun request() = stubAppointmentRequest(businessId = businessId)
    }

    @Test
    fun `flow emits pending requests from db sorted by date`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = fixture.businessId
        val earlier = stubAppointmentRequest(businessId = businessId)
            .copy(date = Instant.fromEpochMilliseconds(0))
        val later = stubAppointmentRequest(businessId = businessId)
            .copy(date = Instant.fromEpochMilliseconds(1000))
        every {
            fixture.dataSource.observeAppointmentRequestsDBChanges(businessId)
        } returns flowOf(listOf(later, earlier))

        whenn()
        val result = fixture.sut.flow(businessId).first()

        then()
        assertEquals(listOf(earlier, later), result)
    }

    @Test
    fun `flow excludes requests that are not pending`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = fixture.businessId
        val pending = stubAppointmentRequest(businessId = businessId)
        val approved = stubAppointmentRequest(businessId = businessId)
            .copy(status = AppointmentRequestStatus.APPROVED)
        every {
            fixture.dataSource.observeAppointmentRequestsDBChanges(businessId)
        } returns flowOf(listOf(pending, approved))

        whenn()
        val result = fixture.sut.flow(businessId).first()

        then()
        assertEquals(listOf(pending), result)
    }

    @Test
    fun `flow re-resolves when the db observation emits again`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = fixture.businessId
        val emissions = MutableSharedFlow<List<AppointmentRequest>>(replay = 1)
        val first = listOf(stubAppointmentRequest(businessId = businessId))
        val second = listOf(stubAppointmentRequest(businessId = businessId))
        emissions.tryEmit(first)
        every { fixture.dataSource.observeAppointmentRequestsDBChanges(businessId) } returns
            emissions
        val results = mutableListOf<List<AppointmentRequest>>()
        val job = launch(Dispatchers.Unconfined) {
            fixture.sut.flow(businessId).collect { results.add(it) }
        }

        whenn()
        emissions.emit(second)

        then()
        job.cancel()
        assertEquals(first, results.first())
        assertEquals(second, results.last())
    }

    @Test
    fun `flow never triggers a network fetch as a side effect of being collected`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = fixture.businessId
        every { fixture.dataSource.observeAppointmentRequestsDBChanges(businessId) } returns
            flowOf(emptyList())

        whenn()
        fixture.sut.flow(businessId).first()

        then()
        verifySuspend(VerifyMode.exactly(0)) {
            fixture.dataSource.getAssignedAppointmentRequests(any(), any(), any())
        }
    }

    @Test
    fun `refresh fetches the first page of the employee's requests and saves it`() = runUnitTest {
        given()
        val fixture = Fixture()
        val page = Page(listOf(fixture.request()), nextCursor = null)
        fixture.stubPage(cursor = null, page)
        fixture.stubCachedIds(page.items.map { it.id })
        fixture.stubWrites()

        whenn()
        val result = fixture.sut.refresh(fixture.businessId)

        then()
        assertEquals(page, result)
        verifySuspend(VerifyMode.exactly(1)) {
            fixture.dataSource.saveAppointmentRequestsInDB(page.items)
        }
        verifySuspend(VerifyMode.exactly(1)) {
            fixture.dataSource.saveLastSyncedAt(fixture.businessId)
        }
    }

    @Test
    fun `refresh deletes cached requests missing from the first page`() = runUnitTest {
        given()
        val fixture = Fixture()
        val stillPresent = fixture.request()
        val staleId = Uuid.random()
        fixture.stubPage(cursor = null, Page(listOf(stillPresent), nextCursor = "after-1"))
        fixture.stubCachedIds(listOf(stillPresent.id, staleId))
        fixture.stubWrites()

        whenn()
        fixture.sut.refresh(fixture.businessId)

        then()
        verifySuspend(VerifyMode.exactly(1)) {
            fixture.dataSource.deleteAppointmentRequestsInDb(listOf(staleId))
        }
    }

    @Test
    fun `refresh does not call delete when nothing is stale`() = runUnitTest {
        given()
        val fixture = Fixture()
        val request = fixture.request()
        fixture.stubPage(cursor = null, Page(listOf(request), nextCursor = null))
        fixture.stubCachedIds(listOf(request.id))
        fixture.stubWrites()

        whenn()
        fixture.sut.refresh(fixture.businessId)

        then()
        verifySuspend(VerifyMode.exactly(0)) {
            fixture.dataSource.deleteAppointmentRequestsInDb(any())
        }
    }

    @Test
    fun `refresh propagates a fetch error and keeps the cache`() = runUnitTest {
        given()
        val fixture = Fixture()
        val error = IllegalStateException("network down")
        everySuspend {
            fixture.dataSource.getAssignedAppointmentRequests(any(), any(), any())
        } throws error

        whenn()
        val thrown = assertFailsWith<IllegalStateException> {
            fixture.sut.refresh(fixture.businessId)
        }

        then()
        assertEquals(error, thrown)
        verifySuspend(VerifyMode.exactly(0)) {
            fixture.dataSource.saveAppointmentRequestsInDB(any())
        }
    }

    @Test
    fun `refresh fails when the user is not an employee of the business`() = runUnitTest {
        given()
        val fixture = Fixture(employeeId = null)

        whenn()
        assertFailsWith<Error.InvalidApplicationState> { fixture.sut.refresh(fixture.businessId) }

        then()
        verifySuspend(VerifyMode.exactly(0)) {
            fixture.dataSource.getAssignedAppointmentRequests(any(), any(), any())
        }
    }

    @Test
    fun `load more fetches the page after the given cursor and adds it to the cache`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val page = Page(listOf(fixture.request()), nextCursor = "after-2")
            fixture.stubPage(cursor = "after-1", page)
            fixture.stubWrites()

            whenn()
            val result = fixture.sut.loadMore(fixture.businessId, cursor = "after-1")

            then()
            assertEquals(page, result)
            verifySuspend(VerifyMode.exactly(1)) {
                fixture.dataSource.saveAppointmentRequestsInDB(page.items)
            }
            verifySuspend(VerifyMode.exactly(0)) {
                fixture.dataSource.deleteAppointmentRequestsInDb(any())
            }
        }

    @Test
    fun `load more restarts from the first page when the cursor is rejected`() = runUnitTest {
        given()
        val fixture = Fixture()
        val firstPage = Page(listOf(fixture.request()), nextCursor = "after-1")
        everySuspend {
            fixture.dataSource.getAssignedAppointmentRequests(
                fixture.businessId,
                fixture.employeeId!!,
                "stale",
            )
        } throws Error.BusinessError(PageErrorCodes.CURSOR_INVALID, "")
        fixture.stubPage(cursor = null, firstPage)
        fixture.stubCachedIds(emptyList())
        fixture.stubWrites()

        whenn()
        val result = fixture.sut.loadMore(fixture.businessId, cursor = "stale")

        then()
        assertEquals(firstPage, result)
        verifySuspend(VerifyMode.exactly(1)) {
            fixture.dataSource.saveAppointmentRequestsInDB(firstPage.items)
        }
    }
}
