package me.bookk.feature.clients.presentation.list

import me.bookk.feature.clients.domain.api.entity.ClientsPermissions
import me.bookk.feature.clients.domain.api.GetClientsPermissions
import kotlinx.coroutines.CompletableDeferred
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.designsystem.resources.DesignSystem
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import library.device.api.DeviceFacade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.designsystem.test.assertEmpty
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.test.tap
import me.bookk.android.feature.clients.resources.ClientsRes
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.FakeTextCollator
import me.bookk.designsystem.uistate.AppBarActionState
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.clients.domain.api.GetDeviceContacts
import me.bookk.feature.clients.domain.api.ImportClients
import me.bookk.feature.clients.domain.api.entity.ClientImportResult
import me.bookk.feature.clients.domain.api.entity.ClientImportEntry
import me.bookk.feature.clients.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.clients.domain.api.entity.Client
import library.permissions.api.PermissionType
import me.bookk.feature.clients.presentation.FakeClientsStateFactory
import me.bookk.feature.clients.presentation.FakePermissionChecker
import me.bookk.feature.clients.presentation.permissionManagerOf
import me.bookk.feature.clients.presentation.stubDetachedClient
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class ClientsListViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private class Fixture(canEdit: Boolean = true) {
        val businessId = Uuid.random()
        val editableBusinessIds = if (canEdit) mutableSetOf(businessId) else mutableSetOf()
        val clients = MutableStateFlow<List<Client>>(emptyList())
        val currentBusinessId = MutableStateFlow<Uuid?>(businessId)
        val getClientsList = mock<GetClientsList> {
            every { flow() } returns clients
            everySuspend { refresh(any()) } returns emptyList()
        }
        val observeCurrentBusinessId = mock<ObserveCurrentBusinessId> {
            every { invoke() } returns currentBusinessId
        }
        val getClientsPermissions = mock<GetClientsPermissions> {
            everySuspend { invoke(any()) } calls { (id: Uuid) ->
                ClientsPermissions(canEdit = id in editableBusinessIds, canDelete = false)
            }
        }
        val contactsPermission = FakePermissionChecker(PermissionType.CONTACTS)
        var deviceContacts = listOf(
            ClientImportEntry(name = "John", lastName = "Doe", phone = "+48123456789", email = null)
        )
        val getDeviceContacts = mock<GetDeviceContacts> {
            everySuspend { invoke() } calls { deviceContacts }
        }
        val device = mock<DeviceFacade> {
            every { openAppSettings() } returns Unit
        }
        var textCollator = FakeTextCollator()
        val importClients = mock<ImportClients> {
            everySuspend { invoke(any(), any()) } returns ClientImportResult(created = emptyList(), rejected = emptyList())
        }
        val errorMapper = FakeErrorMapper()

        fun sut() = ClientsListViewModel(
            getClientsList = getClientsList,
            observeCurrentBusinessId = observeCurrentBusinessId,
            getClientsPermissions = getClientsPermissions,
            getDeviceContacts = getDeviceContacts,
            textCollator = textCollator,
            importClients = importClients,
            permissionManager = permissionManagerOf(contactsPermission),
            device = device,
            stateFactory = FakeClientsStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    @Test
    fun `refreshes clients of current business on start`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.getClientsList.refresh(fixture.businessId) }
    }

    @Test
    fun `groups clients by capitalized first letter sorted by name`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        advanceUntilIdle()

        whenn()
        fixture.clients.value = listOf(
            stubDetachedClient(name = "bob"),
            stubDetachedClient(name = "Anna"),
            stubDetachedClient(name = "Alex")
        )

        then()
        val sections = sut.uiState.clientsList.items
        assertEquals(listOf("A", "B"), sections.map { it.header })
        assertEquals(listOf("Alex", "Anna"), sections[0].items.map { it.name })
    }

    @Test
    fun `shows error state when first load fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getClientsList.refresh(any()) } throws TestException()

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.clientsList.items.isEmpty())
        assertNotNull(sut.uiState.clientsList.errorState)
    }

    @Test
    fun `filters clients by search query`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        advanceUntilIdle()
        fixture.clients.value = listOf(
            stubDetachedClient(name = "Anna", lastName = "Smith"),
            stubDetachedClient(name = "Bob", lastName = "Stone")
        )

        whenn()
        (sut.uiState.searchField as FakeTextFieldState).type("smi")

        then()
        val sections = sut.uiState.clientsList.items
        assertEquals(listOf("Anna"), sections.flatMap { it.items }.map { it.name })
    }

    @Test
    fun `restores all clients when search is cleared`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        advanceUntilIdle()
        fixture.clients.value = listOf(stubDetachedClient(name = "Anna"), stubDetachedClient(name = "Bob"))
        (sut.uiState.searchField as FakeTextFieldState).type("anna")

        whenn()
        (sut.uiState.searchField as FakeTextFieldState).type("")

        then()
        assertEquals(2, sut.uiState.clientsList.items.flatMap { it.items }.size)
    }

    @Test
    fun `navigates to client details on item click`() = runUnitTest {
        given()
        val fixture = Fixture()
        val client = stubDetachedClient(name = "Anna")
        val sut = fixture.sut()
        advanceUntilIdle()
        fixture.clients.value = listOf(client)

        whenn()
        sut.uiState.clientsList.items.single().onItemClick(client)

        then()
        assertEquals(listOf<ClientsListDestination>(ClientsListDestination.ClientDetails(client.id)), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `shows the import action left of the add action for a user who can edit clients`() = runUnitTest {
        given()
        val fixture = Fixture(canEdit = true)

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(
            listOf(DesignSystem.strings.action_import.desc(), DesignSystem.strings.action_add.desc()),
            sut.uiState.appBar.actions.items.map { it.contentDescription }
        )
    }

    @Test
    fun `hides the import and add actions for a user who cannot edit clients`() = runUnitTest {
        given()
        val fixture = Fixture(canEdit = false)

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.appBar.actions.items.isEmpty())
    }

    @Test
    fun `hides the add action after switching to a business where the user cannot edit clients`() = runUnitTest {
        given()
        val fixture = Fixture(canEdit = true)
        val sut = fixture.sut()

        whenn()
        fixture.currentBusinessId.value = Uuid.random()

        then()
        assertTrue(sut.uiState.appBar.actions.items.isEmpty())
    }

    @Test
    fun `ignores a stale edit access result after the business changes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val firstBusinessPermissions = CompletableDeferred<ClientsPermissions>()
        val secondBusinessId = Uuid.random()
        everySuspend { fixture.getClientsPermissions(fixture.businessId) } calls { firstBusinessPermissions.await() }
        val sut = fixture.sut()
        fixture.currentBusinessId.value = secondBusinessId

        whenn()
        firstBusinessPermissions.complete(ClientsPermissions(canEdit = true, canDelete = true))

        then()
        assertTrue(sut.uiState.appBar.actions.items.isEmpty())
    }

    @Test
    fun `requests contacts permission when import is tapped`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.tapImport()

        then()
        assertEquals(1, fixture.contactsPermission.requestCount)
    }

    @Test
    fun `shows contacts access dialog when contacts permission is denied`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.contactsPermission.granted = false
        val sut = fixture.sut()

        whenn()
        sut.tapImport()

        then()
        val dialog = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(ClientsRes.strings.clients_import_permission_title.desc(), dialog.title)
        verify(VerifyMode.not) { fixture.device.openAppSettings() }
    }

    @Test
    fun `opens app settings when open settings is tapped in contacts access dialog`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.contactsPermission.granted = false
        val sut = fixture.sut()
        sut.tapImport()
        val dialog = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()

        whenn()
        dialog.tap(ActionType.POSITIVE)

        then()
        verify { fixture.device.openAppSettings() }
    }

    @Test
    fun `does not open app settings when contacts access dialog is cancelled`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.contactsPermission.granted = false
        val sut = fixture.sut()
        sut.tapImport()
        val dialog = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()

        whenn()
        dialog.tap(ActionType.CANCEL)

        then()
        verify(VerifyMode.not) { fixture.device.openAppSettings() }
    }

    @Test
    fun `does not show contacts access dialog when contacts permission is granted`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.contactsPermission.granted = true
        val sut = fixture.sut()

        whenn()
        sut.tapImport()

        then()
        sut.uiState.notifications.assertEmpty()
    }

    @Test
    fun `reads device contacts when contacts permission is granted`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.contactsPermission.granted = true
        val sut = fixture.sut()

        whenn()
        sut.tapImport()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.getDeviceContacts() }
    }

    @Test
    fun `does not read device contacts when contacts permission is denied`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.contactsPermission.granted = false
        val sut = fixture.sut()

        whenn()
        sut.tapImport()

        then()
        verifySuspend(VerifyMode.not) { fixture.getDeviceContacts() }
    }

    @Test
    fun `shows error when reading device contacts fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getDeviceContacts() } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.tapImport()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        sut.uiState.notifications.assertSingle<PresentationNotification>()
    }

    @Test
    fun `opens contacts picker when device contacts are read`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.tapImport()

        then()
        assertTrue(sut.uiState.isContactsPickerVisible)
        assertEquals(ClientsRes.strings.clients_import_contacts_title.desc(), sut.uiState.contactsPicker.pickerTitle)
    }

    @Test
    fun `lists device contacts in contacts picker sorted by full name`() = runUnitTest {
        given()
        val fixture = Fixture()
        val anna = ClientImportEntry(name = "anna", lastName = "Smith", phone = "+48111111111", email = null)
        val john = ClientImportEntry(name = "John", lastName = "Doe", phone = null, email = "john@example.com")
        fixture.deviceContacts = listOf(john, anna)
        val sut = fixture.sut()

        whenn()
        sut.tapImport()

        then()
        val options = sut.uiState.contactsPicker.options
        assertEquals(listOf("anna Smith".desc(), "John Doe".desc()), options.map { it.displayName })
        assertEquals(listOf(anna, john), options.map { it.domain })
    }

    @Test
    fun `lists device contacts in contacts picker in collation order`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.textCollator = FakeTextCollator(reverseOrder())
        fixture.deviceContacts = listOf(
            ClientImportEntry(name = "Anna", lastName = "Smith", phone = "+48111111111", email = null),
            ClientImportEntry(name = "Zofia", lastName = "Nowak", phone = "+48222222222", email = null)
        )
        val sut = fixture.sut()

        whenn()
        sut.tapImport()

        then()
        assertEquals(listOf("Zofia Nowak".desc(), "Anna Smith".desc()), sut.uiState.contactsPicker.options.map { it.displayName })
    }

    @Test
    fun `shows no contacts message instead of contacts picker when there are no contacts to import`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.deviceContacts = emptyList()
        val sut = fixture.sut()

        whenn()
        sut.tapImport()

        then()
        assertFalse(sut.uiState.isContactsPickerVisible)
        val message = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(ClientsRes.strings.clients_import_no_contacts_title.desc(), message.title)
    }

    @Test
    fun `names a contact by phone in contacts picker when it has no name`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.deviceContacts = listOf(
            ClientImportEntry(name = " ", lastName = "", phone = "+48123456789", email = "john@example.com")
        )
        val sut = fixture.sut()

        whenn()
        sut.tapImport()

        then()
        assertEquals(listOf("+48123456789".desc()), sut.uiState.contactsPicker.options.map { it.displayName })
    }

    @Test
    fun `names a contact by email in contacts picker when it has no name and phone`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.deviceContacts = listOf(
            ClientImportEntry(name = "", lastName = "", phone = null, email = "john@example.com")
        )
        val sut = fixture.sut()

        whenn()
        sut.tapImport()

        then()
        assertEquals(listOf("john@example.com".desc()), sut.uiState.contactsPicker.options.map { it.displayName })
    }

    @Test
    fun `does not open contacts picker when reading device contacts fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getDeviceContacts() } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.tapImport()

        then()
        assertFalse(sut.uiState.isContactsPickerVisible)
    }

    @Test
    fun `closes contacts picker when contacts are picked`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.tapImport()

        whenn()
        sut.uiState.contactsPicker.onItemsPicked(sut.uiState.contactsPicker.options)

        then()
        assertFalse(sut.uiState.isContactsPickerVisible)
    }

    @Test
    fun `imports picked contacts into current business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val anna = ClientImportEntry(name = "Anna", lastName = "Smith", phone = "+48111111111", email = null)
        val john = ClientImportEntry(name = "John", lastName = "Doe", phone = "+48222222222", email = null)
        fixture.deviceContacts = listOf(anna, john)
        val sut = fixture.sut()
        sut.tapImport()

        whenn()
        sut.pickContacts(john)

        then()
        verifySuspend { fixture.importClients(fixture.businessId, listOf(john)) }
    }

    @Test
    fun `disables import action while picked contacts are imported`() = runUnitTest {
        given()
        val fixture = Fixture()
        val importFinished = CompletableDeferred<ClientImportResult>()
        everySuspend { fixture.importClients(any(), any()) } calls { importFinished.await() }
        val sut = fixture.sut()
        sut.tapImport()

        whenn()
        sut.pickContacts()

        then()
        assertFalse(sut.importAction().isEnabled)
    }

    @Test
    fun `shows loader on import action while picked contacts are imported`() = runUnitTest {
        given()
        val fixture = Fixture()
        val importFinished = CompletableDeferred<ClientImportResult>()
        everySuspend { fixture.importClients(any(), any()) } calls { importFinished.await() }
        val sut = fixture.sut()
        sut.tapImport()

        whenn()
        sut.pickContacts()

        then()
        assertTrue(sut.importAction().isLoading)
    }

    @Test
    fun `hides loader on import action after picked contacts are imported`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.tapImport()

        whenn()
        sut.pickContacts()

        then()
        assertFalse(sut.importAction().isLoading)
    }

    @Test
    fun `hides loader on import action when importing picked contacts fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.importClients(any(), any()) } throws TestException()
        val sut = fixture.sut()
        sut.tapImport()

        whenn()
        sut.pickContacts()

        then()
        assertFalse(sut.importAction().isLoading)
    }

    @Test
    fun `enables import action after picked contacts are imported`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.tapImport()

        whenn()
        sut.pickContacts()

        then()
        assertTrue(sut.importAction().isEnabled)
    }

    @Test
    fun `enables import action when importing picked contacts fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.importClients(any(), any()) } throws TestException()
        val sut = fixture.sut()
        sut.tapImport()

        whenn()
        sut.pickContacts()

        then()
        assertTrue(sut.importAction().isEnabled)
    }

    @Test
    fun `plays success haptic when picked contacts are imported`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.tapImport()

        whenn()
        sut.pickContacts()

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.SuccessHaptic>()
    }

    @Test
    fun `does not play success haptic when importing picked contacts fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.importClients(any(), any()) } throws TestException()
        val sut = fixture.sut()
        sut.tapImport()

        whenn()
        sut.pickContacts()

        then()
        assertTrue(sut.uiState.notifications.presentationNotification.none { it is PresentationNotification.SuccessHaptic })
    }

    @Test
    fun `shows error when importing picked contacts fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.importClients(any(), any()) } throws TestException()
        val sut = fixture.sut()
        sut.tapImport()

        whenn()
        sut.pickContacts()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `navigates to add client for current business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.uiState.appBar.actions.items.single { it.contentDescription == DesignSystem.strings.action_add.desc() }.onClick()

        then()
        assertEquals(listOf<ClientsListDestination>(ClientsListDestination.AddClient(fixture.businessId)), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `resets list and reloads when business changes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val otherBusiness = Uuid.random()
        val sut = fixture.sut()
        advanceUntilIdle()
        fixture.clients.value = listOf(stubDetachedClient(name = "Anna"))

        whenn()
        fixture.currentBusinessId.value = otherBusiness

        then()
        verifySuspend { fixture.getClientsList.refresh(otherBusiness) }
        assertTrue(sut.uiState.clientsList.items.isEmpty())
    }

    @Test
    fun `stops refreshing after pull to refresh completes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        advanceUntilIdle()

        whenn()
        sut.uiState.refreshState.onRefresh()
        advanceUntilIdle()

        then()
        assertFalse(sut.uiState.refreshState.isRefreshing)
    }

    @Test
    fun `shows mapped error when clients observation fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.getClientsList.flow() } returns failOnceThenSuspend()

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
        assertEquals(listOf<ClientsListDestination>(ClientsListDestination.Back), sut.uiState.navigation.navigationDestination)
    }

    private fun ClientsListViewModel.tapImport() {
        importAction().onClick()
    }

    private fun ClientsListViewModel.importAction(): AppBarActionState {
        return uiState.appBar.actions.items.single { it.contentDescription == DesignSystem.strings.action_import.desc() }
    }

    private fun ClientsListViewModel.pickContacts(vararg contacts: ClientImportEntry) {
        val options = uiState.contactsPicker.options
        val picked = if (contacts.isEmpty()) options else options.filter { it.domain in contacts }
        uiState.contactsPicker.onItemsPicked(picked)
    }
}
