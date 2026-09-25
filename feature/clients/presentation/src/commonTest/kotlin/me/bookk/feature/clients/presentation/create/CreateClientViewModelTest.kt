package me.bookk.feature.clients.presentation.create

import dev.mokkery.answering.calls
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
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
import me.bookk.feature.clients.domain.api.CreateClient
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.presentation.FakeClientsStateFactory
import me.bookk.feature.clients.presentation.FakeValidateEmail
import me.bookk.feature.clients.presentation.FakeValidateName
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CreateClientViewModelTest {

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
        val createClient = mock<CreateClient>()
        val errorMapper = FakeErrorMapper()
        val created = Capture.slot<Client>()

        fun sut() = CreateClientViewModel(
            businessId = businessId,
            createClient = createClient,
            validateName = FakeValidateName(),
            validateEmail = FakeValidateEmail(),
            stateFactory = FakeClientsStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    private fun CreateClientViewModel.fillValidForm(email: String = "") {
        (uiState.name as FakeTextFieldState).type("Anna")
        (uiState.lastName as FakeTextFieldState).type("Smith")
        (uiState.phone as FakeTextFieldState).type("+380 50")
        (uiState.email as FakeTextFieldState).type(email)
    }

    @Test
    fun `disables submit initially`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.submit.isEnabled)
    }

    @Test
    fun `enables submit when name last name and phone are valid and email is empty`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.fillValidForm()

        then()
        assertTrue(sut.uiState.submit.isEnabled)
    }

    @Test
    fun `keeps submit disabled with invalid email`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.fillValidForm(email = "not-an-email")

        then()
        assertFalse(sut.uiState.email.isValid)
        assertFalse(sut.uiState.submit.isEnabled)
    }

    @Test
    fun `keeps submit disabled with one letter last name`() = runUnitTest {
        given()
        val sut = Fixture().sut()
        sut.fillValidForm()

        whenn()
        (sut.uiState.lastName as FakeTextFieldState).type("S")

        then()
        assertFalse(sut.uiState.submit.isEnabled)
    }

    @Test
    fun `strips non digit characters from phone`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        (sut.uiState.phone as FakeTextFieldState).type("+38 (050) 12")

        then()
        assertEquals("+3805012", sut.uiState.phone.text)
    }

    @Test
    fun `creates detached client and navigates to its details`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createClient(capture(fixture.created)) } calls { (client: Client) -> client }
        val sut = fixture.sut()
        sut.fillValidForm(email = "anna@example.com")

        whenn()
        sut.uiState.submit.onClick?.invoke()

        then()
        val client = assertIs<Client.Detached>(fixture.created.get())
        assertEquals("Anna", client.name)
        assertEquals("Smith", client.lastName)
        assertEquals("+38050", client.phone)
        assertEquals("anna@example.com", client.email)
        assertEquals(fixture.businessId, client.businessId)
        assertEquals(listOf<CreateClientDestination>(CreateClientDestination.Details(client.id)), sut.uiState.navigation.navigationDestination)
        assertFalse(sut.uiState.submit.isLoading)
    }

    @Test
    fun `shows mapped error when creation fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createClient(any()) } throws TestException()
        val sut = fixture.sut()
        sut.fillValidForm()

        whenn()
        sut.uiState.submit.onClick?.invoke()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
        assertFalse(sut.uiState.submit.isLoading)
    }

    @Test
    fun `pushes back destination on back click`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(listOf<CreateClientDestination>(CreateClientDestination.Back), sut.uiState.navigation.navigationDestination)
    }
}
