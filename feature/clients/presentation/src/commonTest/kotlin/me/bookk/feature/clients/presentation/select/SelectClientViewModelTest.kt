package me.bookk.feature.clients.presentation.select

import dev.icerock.moko.resources.desc.desc
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.advanceUntilIdle
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.clients.resources.ClientsRes
import me.bookk.core.domain.pagination.Page
import me.bookk.core.presentation.VmArgs
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.clients.domain.api.GetClientsPermissions
import me.bookk.feature.clients.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.api.entity.ClientsPermissions
import me.bookk.feature.clients.domain.api.entity.ClientsQuery
import me.bookk.feature.clients.presentation.FakeClientsStateFactory
import me.bookk.feature.clients.presentation.stubDetachedClient
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class SelectClientViewModelTest {

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
        val clients = MutableStateFlow<List<Client>>(emptyList())
        val getClientsList = mock<GetClientsList> {
            every { flow(any()) } calls { (search: String?) -> clients.matching(search) }
            everySuspend { refresh(any()) } returns Page(emptyList(), nextCursor = null)
        }
        val observeCurrentBusinessId = mock<ObserveCurrentBusinessId> {
            every { invoke() } returns MutableStateFlow<Uuid?>(businessId)
        }
        val getClientsPermissions = mock<GetClientsPermissions> {
            everySuspend { invoke(businessId) } returns ClientsPermissions(
                canEdit = canEdit,
                canDeleteDetached = false,
                canDeleteIntegrated = false,
            )
        }

        fun sut() = SelectClientViewModel(
            getClientsList = getClientsList,
            observeCurrentBusinessId = observeCurrentBusinessId,
            getClientsPermissions = getClientsPermissions,
            stateFactory = FakeClientsStateFactory(),
            vmArgs = VmArgs(FakeErrorMapper()),
        )
    }

    @Test
    fun `is titled select client`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(ClientsRes.strings.clients_select_title.desc(), sut.uiState.appBar.title)
    }

    @Test
    fun `loads the first page of the current business's clients`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.getClientsList.refresh(ClientsQuery(fixture.businessId)) }
    }

    @Test
    fun `lists the clients the flow emits`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        advanceUntilIdle()

        whenn()
        fixture.clients.value = listOf(
            stubDetachedClient(name = "Olga", lastName = "Nowak"),
            stubDetachedClient(name = "Anna", lastName = "Smith"),
        )

        then()
        assertEquals(
            listOf("Olga Nowak", "Anna Smith"),
            sut.uiState.clients.items.map { it.fullName },
        )
    }

    @Test
    fun `searches the clients for the typed text`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        advanceUntilIdle()
        fixture.clients.value = listOf(
            stubDetachedClient(name = "Olga", lastName = "Nowak"),
            stubDetachedClient(name = "Anna", lastName = "Smith"),
        )

        whenn()
        (sut.uiState.searchField as FakeTextFieldState).type("anna")
        advanceUntilIdle()

        then()
        assertEquals(listOf("Anna Smith"), sut.uiState.clients.items.map { it.fullName })
        verifySuspend {
            fixture.getClientsList.refresh(ClientsQuery(fixture.businessId, "anna"))
        }
    }

    @Test
    fun `offers more clients when the first page has more`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getClientsList.refresh(any()) } returns
            Page(listOf(stubDetachedClient()), nextCursor = "after-1")

        whenn()
        val sut = fixture.sut()
        advanceUntilIdle()

        then()
        assertNotNull(sut.uiState.clients.loadMore)
    }

    @Test
    fun `selects the tapped client`() = runUnitTest {
        given()
        val fixture = Fixture()
        val client = stubDetachedClient(name = "Anna")
        val sut = fixture.sut()
        advanceUntilIdle()
        fixture.clients.value = listOf(client)

        whenn()
        sut.uiState.clients.items.single().onClick()

        then()
        assertEquals(
            listOf<SelectClientDestination>(SelectClientDestination.Selected(client.id)),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `offers adding a client to a user who can edit clients`() = runUnitTest {
        given()
        val fixture = Fixture(canEdit = true)

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(
            listOf(DesignSystem.strings.action_add.desc()),
            sut.uiState.appBar.actions.items.map { it.contentDescription },
        )
    }

    @Test
    fun `does not offer adding a client to a user who cannot edit clients`() = runUnitTest {
        given()
        val fixture = Fixture(canEdit = false)

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.appBar.actions.items.isEmpty())
    }

    @Test
    fun `opens adding a client to the current business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.uiState.appBar.actions.items.single().onClick()

        then()
        assertEquals(
            listOf<SelectClientDestination>(SelectClientDestination.AddClient(fixture.businessId)),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `goes back without a client on back click`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(
            listOf<SelectClientDestination>(SelectClientDestination.Back),
            sut.uiState.navigation.navigationDestination,
        )
    }
}

private fun MutableStateFlow<List<Client>>.matching(search: String?): Flow<List<Client>> {
    return map { clients ->
        clients.filter { search.isNullOrBlank() || it.fullName.contains(search.trim(), true) }
    }
}
