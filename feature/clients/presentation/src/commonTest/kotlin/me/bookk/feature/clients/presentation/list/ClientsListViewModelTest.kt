package me.bookk.feature.clients.presentation.list

import me.bookk.feature.clients.domain.api.entity.ClientsPermissions
import me.bookk.feature.clients.domain.api.GetClientsPermissions
import kotlinx.coroutines.CompletableDeferred
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import me.bookk.core.presentation.VmArgs
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.clients.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.presentation.FakeClientsStateFactory
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
        val errorMapper = FakeErrorMapper()

        fun sut() = ClientsListViewModel(
            getClientsList = getClientsList,
            observeCurrentBusinessId = observeCurrentBusinessId,
            getClientsPermissions = getClientsPermissions,
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
    fun `shows the add action for a user who can edit clients`() = runUnitTest {
        given()
        val fixture = Fixture(canEdit = true)

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(1, sut.uiState.appBar.actions.items.size)
    }

    @Test
    fun `hides the add action for a user who cannot edit clients`() = runUnitTest {
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
    fun `navigates to add client for current business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.uiState.appBar.actions.items.single().onClick()

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
}
