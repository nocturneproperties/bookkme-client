package me.bookk.feature.employees.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
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
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import me.bookk.feature.employees.domain.datasource.EmployeeInvitationDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetEmployeeInvitationsImplTest {

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
        val dataSource = mock<EmployeeInvitationDataSource>()
        val sut = GetEmployeeInvitationsImpl(dataSource)
    }

    @Test
    fun `returns invitations from remote`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val invitations = listOf(stubEmployeeInvitation(businessId), stubEmployeeInvitation(businessId))
        everySuspend { fixture.dataSource.getInvitations(businessId) } returns invitations
        everySuspend { fixture.dataSource.deleteInvitationsInDb() } returns Unit
        everySuspend { fixture.dataSource.saveInvitationsInDb(invitations) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(invitations, result)
    }

    @Test
    fun `deletes old invitations then saves new ones and marks synced`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val invitations = listOf(stubEmployeeInvitation(businessId))
        everySuspend { fixture.dataSource.getInvitations(businessId) } returns invitations
        everySuspend { fixture.dataSource.deleteInvitationsInDb() } returns Unit
        everySuspend { fixture.dataSource.saveInvitationsInDb(invitations) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit

        whenn()
        fixture.sut(businessId)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.deleteInvitationsInDb() }
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveInvitationsInDb(invitations) }
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveLastSyncedAt(businessId) }
    }

    @Test
    fun `cached calls onResultAvailable with DB then remote when business synced before`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val cached = listOf(stubEmployeeInvitation(businessId))
        val remote = listOf(stubEmployeeInvitation(businessId), stubEmployeeInvitation(businessId))
        everySuspend { fixture.dataSource.getLastSyncedAt(businessId) } returns Instant.fromEpochMilliseconds(0)
        everySuspend { fixture.dataSource.getInvitationsFromDb(businessId) } returns cached
        everySuspend { fixture.dataSource.getInvitations(businessId) } returns remote
        everySuspend { fixture.dataSource.deleteInvitationsInDb() } returns Unit
        everySuspend { fixture.dataSource.saveInvitationsInDb(remote) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit
        val received = mutableListOf<List<EmployeeInvitation>>()

        whenn()
        fixture.sut.cached(businessId) { received.add(it) }

        then()
        assertEquals<List<List<EmployeeInvitation>>>(listOf(cached, remote), received)
    }

    @Test
    fun `cached skips DB callback when business never synced before, even if DB has stale rows`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val remote = listOf(stubEmployeeInvitation(businessId))
        everySuspend { fixture.dataSource.getLastSyncedAt(businessId) } returns null
        everySuspend { fixture.dataSource.getInvitations(businessId) } returns remote
        everySuspend { fixture.dataSource.deleteInvitationsInDb() } returns Unit
        everySuspend { fixture.dataSource.saveInvitationsInDb(remote) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit
        val received = mutableListOf<List<EmployeeInvitation>>()

        whenn()
        fixture.sut.cached(businessId) { received.add(it) }

        then()
        assertEquals<List<List<EmployeeInvitation>>>(listOf(remote), received)
    }

    @Test
    fun `cached calls onResultAvailable with empty DB list when synced before and business has no invitations`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val businessId = Uuid.random()
            everySuspend { fixture.dataSource.getLastSyncedAt(businessId) } returns Instant.fromEpochMilliseconds(0)
            everySuspend { fixture.dataSource.getInvitationsFromDb(businessId) } returns emptyList()
            everySuspend { fixture.dataSource.getInvitations(businessId) } returns emptyList()
            everySuspend { fixture.dataSource.deleteInvitationsInDb() } returns Unit
            everySuspend { fixture.dataSource.saveInvitationsInDb(emptyList()) } returns Unit
            everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit
            val received = mutableListOf<List<EmployeeInvitation>>()

            whenn()
            fixture.sut.cached(businessId) { received.add(it) }

            then()
            assertEquals<List<List<EmployeeInvitation>>>(listOf(emptyList(), emptyList()), received)
        }
}
