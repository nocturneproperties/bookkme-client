package me.bookk.feature.clients.presentation.details

import dev.icerock.moko.resources.desc.desc
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import kotlinx.coroutines.flow.MutableStateFlow
import library.device.api.DeviceFacade
import me.bookk.core.presentation.VmArgs
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.feature.clients.domain.api.GetClient
import me.bookk.feature.clients.domain.api.GetClientsPermissions
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.api.entity.ClientsPermissions
import me.bookk.feature.clients.presentation.FakeClientsStateFactory
import me.bookk.feature.clients.presentation.stubDetachedClient
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class ClientDetailsViewModelTest {

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
        val id = Uuid.random()
        val client = MutableStateFlow<Client?>(null)
        val getClient = mock<GetClient> {
            every { flow(id) } returns client
        }
        val getClientsPermissions = mock<GetClientsPermissions> {
            everySuspend { invoke(any()) } returns ClientsPermissions(canEdit = canEdit, canDelete = false)
        }
        val device = mock<DeviceFacade> {
            every { dial(any()) } returns Unit
            every { mail(any()) } returns Unit
        }
        val errorMapper = FakeErrorMapper()

        fun sut() = ClientDetailsViewModel(
            id = id,
            getClient = getClient,
            getClientsPermissions = getClientsPermissions,
            device = device,
            stateFactory = FakeClientsStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    @Test
    fun `renders client name and contact lines`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.client.value = stubDetachedClient(id = fixture.id, name = "Anna", lastName = "Smith")

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals("Anna Smith".desc(), sut.uiState.appBar.title)
        assertEquals(3, sut.uiState.infoSections.items.size)
    }

    @Test
    fun `dials client phone on phone line click`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.client.value = stubDetachedClient(id = fixture.id, phone = "+380501")
        val sut = fixture.sut()

        whenn()
        sut.uiState.infoSections.items[0].onClick?.invoke()

        then()
        verify { fixture.device.dial("+380501") }
    }

    @Test
    fun `mails client on email line click`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.client.value = stubDetachedClient(id = fixture.id, email = "a@b.c")
        val sut = fixture.sut()

        whenn()
        sut.uiState.infoSections.items[1].onClick?.invoke()

        then()
        verify { fixture.device.mail("a@b.c") }
    }

    @Test
    fun `does not dial when client has no phone`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.client.value = stubDetachedClient(id = fixture.id, phone = null)
        val sut = fixture.sut()

        whenn()
        sut.uiState.infoSections.items[0].onClick?.invoke()

        then()
        verify(VerifyMode.not) { fixture.device.dial(any()) }
    }

    @Test
    fun `re-renders when the stored client changes`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.client.value = stubDetachedClient(id = fixture.id, name = "Anna")
        val sut = fixture.sut()

        whenn()
        fixture.client.value = stubDetachedClient(id = fixture.id, name = "Maria")

        then()
        assertEquals("Maria Smith".desc(), sut.uiState.appBar.title)
    }

    @Test
    fun `shows mapped error when client cannot be loaded`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.getClient.flow(any()) } returns failOnceThenSuspend()

        whenn()
        fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `shows the edit action for a user who can edit clients`() = runUnitTest {
        given()
        val fixture = Fixture(canEdit = true)
        fixture.client.value = stubDetachedClient(id = fixture.id)

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(1, sut.uiState.appBar.actions.items.size)
    }

    @Test
    fun `hides the edit action for a user who cannot edit clients`() = runUnitTest {
        given()
        val fixture = Fixture(canEdit = false)
        fixture.client.value = stubDetachedClient(id = fixture.id)

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.appBar.actions.items.isEmpty())
    }

    @Test
    fun `hides the edit action while the client is loading`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.appBar.actions.items.isEmpty())
    }

    @Test
    fun `navigates to edit on edit action`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.client.value = stubDetachedClient(id = fixture.id)
        val sut = fixture.sut()

        whenn()
        sut.uiState.appBar.actions.items.single().onClick()

        then()
        assertEquals(listOf<ClientDetailsDestination>(ClientDetailsDestination.Edit(fixture.id)), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `pushes back destination on back click`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.client.value = stubDetachedClient(id = fixture.id)
        val sut = fixture.sut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(listOf<ClientDetailsDestination>(ClientDetailsDestination.Back), sut.uiState.navigation.navigationDestination)
    }
}
