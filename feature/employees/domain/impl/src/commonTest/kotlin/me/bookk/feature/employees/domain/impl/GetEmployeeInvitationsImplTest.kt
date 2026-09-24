package me.bookk.feature.employees.domain.impl

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
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import me.bookk.feature.employees.domain.datasource.EmployeeInvitationDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
    fun `flow emits the db invitations for the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val invitations = listOf(stubEmployeeInvitation(businessId))
        every { fixture.dataSource.observeInvitationsDBChanges(businessId) } returns flowOf(invitations)

        whenn()
        val result = fixture.sut.flow(businessId).first()

        then()
        assertEquals(invitations, result)
    }

    @Test
    fun `flow re-resolves when the db observation emits again`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val emissions = MutableSharedFlow<List<EmployeeInvitation>>(replay = 1)
        val first = listOf(stubEmployeeInvitation(businessId))
        val second = listOf(stubEmployeeInvitation(businessId))
        emissions.tryEmit(first)
        every { fixture.dataSource.observeInvitationsDBChanges(businessId) } returns emissions
        val results = mutableListOf<List<EmployeeInvitation>>()
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
        every { fixture.dataSource.observeInvitationsDBChanges(businessId) } returns flowOf(emptyList())

        whenn()
        fixture.sut.flow(businessId).first()

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.getInvitations(any()) }
    }

    @Test
    fun `refresh fetches invitations from remote and saves them`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val invitations = listOf(stubEmployeeInvitation(businessId))
        everySuspend { fixture.dataSource.getInvitations(businessId) } returns invitations
        everySuspend { fixture.dataSource.getInvitationIdsInDb(businessId) } returns invitations.map { it.id }
        everySuspend { fixture.dataSource.saveInvitationsInDb(invitations) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit

        whenn()
        val result = fixture.sut.refresh(businessId)

        then()
        assertEquals(invitations, result)
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveInvitationsInDb(invitations) }
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveLastSyncedAt(businessId) }
    }

    @Test
    fun `refresh deletes local invitations that are no longer present remotely`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val stillPresent = stubEmployeeInvitation(businessId)
        val remote = listOf(stillPresent)
        val staleId = Uuid.random()
        everySuspend { fixture.dataSource.getInvitations(businessId) } returns remote
        everySuspend {
            fixture.dataSource.getInvitationIdsInDb(businessId)
        } returns listOf(stillPresent.id, staleId)
        everySuspend { fixture.dataSource.deleteInvitationsInDb(listOf(staleId)) } returns Unit
        everySuspend { fixture.dataSource.saveInvitationsInDb(remote) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit

        whenn()
        fixture.sut.refresh(businessId)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.deleteInvitationsInDb(listOf(staleId)) }
    }

    @Test
    fun `refresh does not call delete when nothing is stale`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val invitation = stubEmployeeInvitation(businessId)
        everySuspend { fixture.dataSource.getInvitations(businessId) } returns listOf(invitation)
        everySuspend { fixture.dataSource.getInvitationIdsInDb(businessId) } returns listOf(invitation.id)
        everySuspend { fixture.dataSource.saveInvitationsInDb(listOf(invitation)) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit

        whenn()
        fixture.sut.refresh(businessId)

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.deleteInvitationsInDb(any()) }
    }

    @Test
    fun `refresh propagates a fetch error to the caller`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val error = IllegalStateException("network down")
        everySuspend { fixture.dataSource.getInvitations(businessId) } throws error

        whenn()
        val thrown = assertFailsWith<IllegalStateException> { fixture.sut.refresh(businessId) }

        then()
        assertEquals(error, thrown)
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.getInvitationIdsInDb(any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveInvitationsInDb(any()) }
    }
}
