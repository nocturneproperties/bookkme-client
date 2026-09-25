package me.bookk.feature.clients.presentation.edit

import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.test.tap
import me.bookk.feature.clients.domain.api.DeleteClient
import me.bookk.feature.clients.domain.api.EditClient
import me.bookk.feature.clients.domain.api.GetClient
import me.bookk.feature.clients.domain.api.GetClientsPermissions
import me.bookk.feature.clients.domain.api.entity.ClientsPermissions
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.presentation.FakeClientsStateFactory
import me.bookk.feature.clients.presentation.FakeValidateEmail
import me.bookk.feature.clients.presentation.FakeValidateName
import me.bookk.feature.clients.presentation.stubDetachedClient
import me.bookk.feature.clients.presentation.stubIntegratedClient
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class EditClientViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private class Fixture(canDelete: Boolean = true) {
        val id = Uuid.random()
        val getClient = mock<GetClient>()
        val getClientsPermissions = mock<GetClientsPermissions> {
            everySuspend { invoke(any()) } returns ClientsPermissions(canEdit = true, canDelete = canDelete)
        }
        val editClient = mock<EditClient>()
        val deleteClient = mock<DeleteClient>()
        val errorMapper = FakeErrorMapper()
        val edited = Capture.slot<Client>()

        fun sut() = EditClientViewModel(
            id = id,
            getClient = getClient,
            editClient = editClient,
            deleteClient = deleteClient,
            getClientsPermissions = getClientsPermissions,
            validateName = FakeValidateName(),
            validateEmail = FakeValidateEmail(),
            stateFactory = FakeClientsStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )

        fun sutWith(client: Client): EditClientViewModel {
            everySuspend { getClient(any()) } returns client
            return sut()
        }
    }

    @Test
    fun `renders detached client as editable`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(stubDetachedClient(name = "Anna", description = "VIP"))

        then()
        assertEquals("Anna", sut.uiState.name.text)
        assertEquals("VIP", sut.uiState.description.text)
        assertTrue(sut.uiState.name.enabled)
        assertFalse(sut.uiState.isAttachedInfoVisible)
        assertTrue(sut.uiState.submit.isEnabled)
    }

    @Test
    fun `locks contact fields of integrated client`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(stubIntegratedClient())

        then()
        assertTrue(sut.uiState.isAttachedInfoVisible)
        assertFalse(sut.uiState.name.enabled)
        assertFalse(sut.uiState.lastName.enabled)
        assertFalse(sut.uiState.phone.enabled)
        assertFalse(sut.uiState.email.enabled)
    }

    @Test
    fun `disables submit for invalid email`() = runUnitTest {
        given()
        val sut = Fixture().sutWith(stubDetachedClient())

        whenn()
        (sut.uiState.email as FakeTextFieldState).type("broken")

        then()
        assertFalse(sut.uiState.submit.isEnabled)
    }

    @Test
    fun `saves edited detached client and goes back`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.editClient(capture(fixture.edited)) } calls { (client: Client) -> client }
        val sut = fixture.sutWith(stubDetachedClient(name = "Anna"))
        (sut.uiState.name as FakeTextFieldState).type("Maria")
        (sut.uiState.description as FakeTextFieldState).type("Prefers mornings")

        whenn()
        sut.uiState.submit.onClick?.invoke()

        then()
        val saved = assertIs<Client.Detached>(fixture.edited.get())
        assertEquals("Maria", saved.name)
        assertEquals("Prefers mornings", saved.description)
        assertEquals(listOf<EditClientDestination>(EditClientDestination.Back), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `saves only description of integrated client`() = runUnitTest {
        given()
        val fixture = Fixture()
        val original = stubIntegratedClient(name = "Olga")
        everySuspend { fixture.editClient(capture(fixture.edited)) } calls { (client: Client) -> client }
        val sut = fixture.sutWith(original)
        (sut.uiState.name as FakeTextFieldState).type("Changed")
        (sut.uiState.description as FakeTextFieldState).type("Note")

        whenn()
        sut.uiState.submit.onClick?.invoke()

        then()
        assertEquals(original.copy(description = "Note"), fixture.edited.get())
    }

    @Test
    fun `shows mapped error when save fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.editClient(any()) } throws TestException()
        val sut = fixture.sutWith(stubDetachedClient())

        whenn()
        sut.uiState.submit.onClick?.invoke()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertFalse(sut.uiState.submit.isLoading)
        assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
    }

    @Test
    fun `shows delete for a user who can delete clients`() = runUnitTest {
        given()
        val fixture = Fixture(canDelete = true)

        whenn()
        val sut = fixture.sutWith(stubDetachedClient())

        then()
        assertTrue(sut.uiState.deleteButton.isVisible)
    }

    @Test
    fun `hides delete for a user who cannot delete clients`() = runUnitTest {
        given()
        val fixture = Fixture(canDelete = false)

        whenn()
        val sut = fixture.sutWith(stubDetachedClient())

        then()
        assertFalse(sut.uiState.deleteButton.isVisible)
    }

    @Test
    fun `checks delete permission in the business of the client`() = runUnitTest {
        given()
        val fixture = Fixture()
        val client = stubDetachedClient()

        whenn()
        fixture.sutWith(client)

        then()
        verifySuspend { fixture.getClientsPermissions(client.businessId) }
    }

    @Test
    fun `hides delete while the client is loading`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getClient(any()) } throws TestException()

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.deleteButton.isVisible)
    }

    @Test
    fun `asks for confirmation before deleting`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sutWith(stubDetachedClient())

        whenn()
        sut.uiState.deleteButton.onClick?.invoke()

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        verifySuspend(VerifyMode.not) { fixture.deleteClient(any()) }
    }

    @Test
    fun `deletes client after confirmation`() = runUnitTest {
        given()
        val fixture = Fixture()
        val client = stubDetachedClient()
        everySuspend { fixture.deleteClient(any()) } returns Unit
        val sut = fixture.sutWith(client)
        sut.uiState.deleteButton.onClick?.invoke()

        whenn()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>().tap(ActionType.NEGATIVE)

        then()
        verifySuspend { fixture.deleteClient(client) }
        assertEquals(listOf<EditClientDestination>(EditClientDestination.Deleted), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `shows mapped error when delete fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.deleteClient(any()) } throws TestException()
        val sut = fixture.sutWith(stubDetachedClient())
        sut.uiState.deleteButton.onClick?.invoke()

        whenn()
        sut.uiState.notifications.presentationNotification.filterIsInstance<PresentationNotification.Message>().single().tap(ActionType.NEGATIVE)

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertFalse(sut.uiState.deleteButton.isLoading)
    }

    @Test
    fun `shows mapped error when client cannot be loaded`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getClient(any()) } throws TestException()

        whenn()
        fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }
}
