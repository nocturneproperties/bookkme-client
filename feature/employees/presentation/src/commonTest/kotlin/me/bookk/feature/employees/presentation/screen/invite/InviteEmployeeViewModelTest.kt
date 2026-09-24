package me.bookk.feature.employees.presentation.screen.invite

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.datetime.LocalDateTime
import library.device.api.DeviceFacade
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.test.FakeDateLocalizer
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.designsystem.test.tap
import me.bookk.feature.employees.domain.api.CreateEmployeeInvitation
import me.bookk.feature.employees.domain.api.GetEmployeeInvitations
import me.bookk.feature.employees.domain.api.RevokeEmployeeInvitation
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitationStatus
import me.bookk.feature.employees.presentation.FakeEmployeesStateFactory
import me.bookk.feature.employees.presentation.stubInvitation
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.uuid.Uuid

class InviteEmployeeViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private class Fixture {
        val businessId = Uuid.random()
        val invitations = MutableStateFlow<List<EmployeeInvitation>>(emptyList())
        val createEmployeeInvitation = mock<CreateEmployeeInvitation>()
        val getEmployeeInvitations = mock<GetEmployeeInvitations> {
            every { flow(any()) } returns invitations
            everySuspend { refresh(any()) } returns emptyList()
        }
        val revokeEmployeeInvitation = mock<RevokeEmployeeInvitation>()
        val device = mock<DeviceFacade> {
            every { copyToClipboard(any()) } returns Unit
        }
        val errorMapper = FakeErrorMapper()

        fun sut() = InviteEmployeeViewModel(
            businessId = businessId,
            createEmployeeInvitation = createEmployeeInvitation,
            getEmployeeInvitations = getEmployeeInvitations,
            revokeEmployeeInvitation = revokeEmployeeInvitation,
            device = device,
            dateLocalizer = FakeDateLocalizer(),
            stateFactory = FakeEmployeesStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    private suspend fun kotlinx.coroutines.test.TestScope.loadedSut(fixture: Fixture): InviteEmployeeViewModel {
        val sut = fixture.sut()
        advanceUntilIdle()
        return sut
    }

    @Test
    fun `refreshes invitations of business on start`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.getEmployeeInvitations.refresh(fixture.businessId) }
    }

    @Test
    fun `renders invitations newest first with masked code`() = runUnitTest {
        given()
        val fixture = Fixture()
        val older = stubInvitation(createdAt = LocalDateTime(2024, 1, 1, 0, 0))
        val newer = stubInvitation(createdAt = LocalDateTime(2024, 2, 1, 0, 0))
        val sut = loadedSut(fixture)

        whenn()
        fixture.invitations.value = listOf(older, newer)

        then()
        assertEquals(listOf(newer.id, older.id), sut.uiState.invitationsList.items.map { it.id })
        assertEquals(MaskedInvitationCode, sut.uiState.invitationsList.items.first().code)
    }

    @Test
    fun `colors invitation status`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = loadedSut(fixture)

        whenn()
        fixture.invitations.value = listOf(stubInvitation(status = EmployeeInvitationStatus.REDEEMED))

        then()
        assertEquals(ColorToken.Success, sut.uiState.invitationsList.items.single().status.color)
    }

    @Test
    fun `only pending invitations are clickable`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = loadedSut(fixture)

        whenn()
        fixture.invitations.value = listOf(
            stubInvitation(status = EmployeeInvitationStatus.PENDING, createdAt = LocalDateTime(2024, 2, 1, 0, 0)),
            stubInvitation(status = EmployeeInvitationStatus.REVOKED)
        )

        then()
        val items = sut.uiState.invitationsList.items
        assertNotNull(items[0].onClick)
        assertNull(items[1].onClick)
    }

    @Test
    fun `copies new code to clipboard and reloads invitations`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createEmployeeInvitation(any()) } returns stubInvitation(code = "ABCD1234")
        val sut = loadedSut(fixture)

        whenn()
        sut.uiState.generateCodeButton.onClick?.invoke()

        then()
        verify { fixture.device.copyToClipboard("ABCD1234") }
        verifySuspend(VerifyMode.exactly(2)) { fixture.getEmployeeInvitations.refresh(fixture.businessId) }
        val message = sut.uiState.notifications.assertSingle<PresentationNotification.GlobalMessage>()
        assertEquals(PresentationNotification.GlobalMessage.State.SUCCESS, message.state)
        assertFalse(sut.uiState.generateCodeButton.isLoading)
    }

    @Test
    fun `does not copy when created invitation has no code`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createEmployeeInvitation(any()) } returns stubInvitation(code = null)
        val sut = loadedSut(fixture)

        whenn()
        sut.uiState.generateCodeButton.onClick?.invoke()

        then()
        verify(VerifyMode.not) { fixture.device.copyToClipboard(any()) }
    }

    @Test
    fun `shows message when pending invitation limit is reached`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createEmployeeInvitation(any()) } throws CreateEmployeeInvitation.Error.PendingInvitationsLimitReached(TestException())
        val sut = loadedSut(fixture)

        whenn()
        sut.uiState.generateCodeButton.onClick?.invoke()

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
    }

    @Test
    fun `shows message when daily invitation limit is reached`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createEmployeeInvitation(any()) } throws CreateEmployeeInvitation.Error.DailyInvitationsLimitReached(TestException())
        val sut = loadedSut(fixture)

        whenn()
        sut.uiState.generateCodeButton.onClick?.invoke()

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
    }

    @Test
    fun `shows mapped error when generating code fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createEmployeeInvitation(any()) } throws TestException()
        val sut = loadedSut(fixture)

        whenn()
        sut.uiState.generateCodeButton.onClick?.invoke()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `revokes pending invitation after confirmation and reloads`() = runUnitTest {
        given()
        val fixture = Fixture()
        val invitation = stubInvitation(status = EmployeeInvitationStatus.PENDING)
        everySuspend { fixture.revokeEmployeeInvitation(any(), any()) } returns Unit
        val sut = loadedSut(fixture)
        fixture.invitations.value = listOf(invitation)
        sut.uiState.invitationsList.items.single().onClick?.invoke()

        whenn()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>().tap(ActionType.NEGATIVE)

        then()
        verifySuspend { fixture.revokeEmployeeInvitation(fixture.businessId, invitation.id) }
        verifySuspend(VerifyMode.exactly(2)) { fixture.getEmployeeInvitations.refresh(fixture.businessId) }
    }

    @Test
    fun `shows message when invitation was already processed`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.revokeEmployeeInvitation(any(), any()) } throws RevokeEmployeeInvitation.Error.AlreadyProcessed(TestException())
        val sut = loadedSut(fixture)
        fixture.invitations.value = listOf(stubInvitation())
        sut.uiState.invitationsList.items.single().onClick?.invoke()
        val dialog = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        sut.uiState.notifications.removeFirst()

        whenn()
        dialog.tap(ActionType.NEGATIVE)

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
    }

    @Test
    fun `shows mapped error when invitations observation fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.getEmployeeInvitations.flow(any()) } returns failOnceThenSuspend()

        whenn()
        fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `pushes back destination on back click`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(listOf<InviteEmployeeDestinations>(InviteEmployeeDestinations.Back), sut.uiState.navigation.navigationDestination)
    }
}
