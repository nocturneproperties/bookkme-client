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
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestStatus
import me.bookk.feature.appointments.domain.datasource.AppointmentRequestDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetAppointmentRequestsImplTest {

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
        val dataSource = mock<AppointmentRequestDataSource>()
        val sut = GetAppointmentRequestsImpl(dataSource)
    }

    @Test
    fun `flow emits pending requests from db sorted by date`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
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
        val businessId = Uuid.random()
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
        val businessId = Uuid.random()
        val emissions = MutableSharedFlow<List<AppointmentRequest>>(replay = 1)
        val first = listOf(stubAppointmentRequest(businessId = businessId))
        val second = listOf(stubAppointmentRequest(businessId = businessId))
        emissions.tryEmit(first)
        every { fixture.dataSource.observeAppointmentRequestsDBChanges(businessId) } returns emissions
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
        val businessId = Uuid.random()
        every { fixture.dataSource.observeAppointmentRequestsDBChanges(businessId) } returns flowOf(emptyList())

        whenn()
        fixture.sut.flow(businessId).first()

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.getAppointmentRequests(any()) }
    }

    @Test
    fun `refresh fetches requests from remote and saves them`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val requests = listOf(stubAppointmentRequest(businessId = businessId))
        everySuspend { fixture.dataSource.getAppointmentRequests(businessId) } returns requests
        everySuspend {
            fixture.dataSource.getAppointmentRequestIdsInDb(businessId)
        } returns requests.map { it.id }
        everySuspend { fixture.dataSource.saveAppointmentRequestsInDB(requests) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit

        whenn()
        val result = fixture.sut.refresh(businessId)

        then()
        assertEquals(requests, result)
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveAppointmentRequestsInDB(requests) }
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveLastSyncedAt(businessId) }
    }

    @Test
    fun `refresh deletes local requests that are no longer present remotely`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val stillPresent = stubAppointmentRequest(businessId = businessId)
        val remote = listOf(stillPresent)
        val staleId = Uuid.random()
        everySuspend { fixture.dataSource.getAppointmentRequests(businessId) } returns remote
        everySuspend {
            fixture.dataSource.getAppointmentRequestIdsInDb(businessId)
        } returns listOf(stillPresent.id, staleId)
        everySuspend { fixture.dataSource.deleteAppointmentRequestsInDb(listOf(staleId)) } returns Unit
        everySuspend { fixture.dataSource.saveAppointmentRequestsInDB(remote) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit

        whenn()
        fixture.sut.refresh(businessId)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.deleteAppointmentRequestsInDb(listOf(staleId)) }
    }

    @Test
    fun `refresh does not call delete when nothing is stale`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val request = stubAppointmentRequest(businessId = businessId)
        everySuspend { fixture.dataSource.getAppointmentRequests(businessId) } returns listOf(request)
        everySuspend {
            fixture.dataSource.getAppointmentRequestIdsInDb(businessId)
        } returns listOf(request.id)
        everySuspend { fixture.dataSource.saveAppointmentRequestsInDB(listOf(request)) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit

        whenn()
        fixture.sut.refresh(businessId)

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.deleteAppointmentRequestsInDb(any()) }
    }

    @Test
    fun `refresh propagates a fetch error to the caller`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val error = IllegalStateException("network down")
        everySuspend { fixture.dataSource.getAppointmentRequests(businessId) } throws error

        whenn()
        val thrown = assertFailsWith<IllegalStateException> { fixture.sut.refresh(businessId) }

        then()
        assertEquals(error, thrown)
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.getAppointmentRequestIdsInDb(any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveAppointmentRequestsInDB(any()) }
    }
}
