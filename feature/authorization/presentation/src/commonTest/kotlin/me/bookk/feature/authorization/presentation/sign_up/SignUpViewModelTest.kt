package me.bookk.feature.authorization.presentation.sign_up

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import library.device.api.DeviceFacade
import library.validation.api.ValidateEmail
import library.validation.api.ValidateName
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.feature.authorization.domain.api.CreateAccount
import me.bookk.feature.authorization.presentation.FakeAuthStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SignUpViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private class FakeValidateName : ValidateName {
        override fun invoke(name: String): ValidateName.Result {
            return if (name.length >= 2) ValidateName.Result.Valid else ValidateName.Result.Invalid.Length
        }
    }

    private class FakeValidateEmail : ValidateEmail {
        override fun invoke(email: String): ValidateEmail.Result {
            return if ("@" in email) ValidateEmail.Result.Valid else ValidateEmail.Result.Invalid.Format
        }
    }

    private class Fixture {
        val createAccount = mock<CreateAccount>()
        val deviceFacade = mock<DeviceFacade> {
            every { openUrlPreview(any()) } returns Unit
        }
        val errorMapper = FakeErrorMapper()
        val userData = Capture.slot<CreateAccount.UserData>()

        fun sut() = SignUpViewModel(
            validateName = FakeValidateName(),
            validateEmail = FakeValidateEmail(),
            createAccount = createAccount,
            deviceFacade = deviceFacade,
            stateFactory = FakeAuthStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    private fun SignUpViewModel.fillValidForm() {
        onFirstNameTextChanged("Anna")
        onLastNameTextChanged("Smith")
        onEmailTextChanged("anna@example.com")
    }

    @Test
    fun `enables confirm when every field is valid`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.fillValidForm()

        then()
        assertTrue(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `shows error for short first name`() = runUnitTest {
        given()
        val sut = Fixture().sut()
        sut.fillValidForm()

        whenn()
        sut.onFirstNameTextChanged("A")

        then()
        assertEquals(ValidationState.ERROR, sut.uiState.name.validationState)
        assertFalse(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `shows error for invalid email`() = runUnitTest {
        given()
        val sut = Fixture().sut()
        sut.fillValidForm()

        whenn()
        sut.onEmailTextChanged("anna")

        then()
        assertEquals(ValidationState.ERROR, sut.uiState.email.validationState)
        assertFalse(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `creates account with entered data`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createAccount(capture(fixture.userData)) } returns Unit
        val sut = fixture.sut()
        sut.fillValidForm()

        whenn()
        sut.onConfirmButtonClick()

        then()
        val data = fixture.userData.get()
        assertEquals("Anna", data.firstName)
        assertEquals("Smith", data.lastName)
        assertEquals("anna@example.com", data.email)
        assertFalse(sut.uiState.confirmButton.isLoading)
    }

    @Test
    fun `marks email invalid when it already exists`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createAccount(any()) } throws CreateAccount.Error.EmailAlreadyExist()
        val sut = fixture.sut()
        sut.fillValidForm()

        whenn()
        sut.onConfirmButtonClick()

        then()
        assertFalse(sut.uiState.email.isValid)
        assertEquals(ValidationState.ERROR, sut.uiState.email.validationState)
        assertTrue(sut.uiState.notification.presentationNotification.isEmpty())
    }

    @Test
    fun `marks email invalid when server rejects its format`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createAccount(any()) } throws CreateAccount.Error.InvalidEmailFormat()
        val sut = fixture.sut()
        sut.fillValidForm()

        whenn()
        sut.onConfirmButtonClick()

        then()
        assertFalse(sut.uiState.email.isValid)
    }

    @Test
    fun `shows message when passkey verification fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createAccount(any()) } throws CreateAccount.Error.PasskeyVerificationFailed()
        val sut = fixture.sut()
        sut.fillValidForm()

        whenn()
        sut.onConfirmButtonClick()

        then()
        sut.uiState.notification.assertSingle<PresentationNotification.Message>()
    }

    @Test
    fun `shows message when account creation fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createAccount(any()) } throws CreateAccount.Error.AccountCreationFailed()
        val sut = fixture.sut()
        sut.fillValidForm()

        whenn()
        sut.onConfirmButtonClick()

        then()
        sut.uiState.notification.assertSingle<PresentationNotification.Message>()
    }

    @Test
    fun `shows mapped error when sign up fails unexpectedly`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createAccount(any()) } throws TestException()
        val sut = fixture.sut()
        sut.fillValidForm()

        whenn()
        sut.onConfirmButtonClick()

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
        assertEquals(listOf<SignUpNavigationDestination>(SignUpNavigationDestination.Back), sut.uiState.navigation.navigationDestination)
    }
}
