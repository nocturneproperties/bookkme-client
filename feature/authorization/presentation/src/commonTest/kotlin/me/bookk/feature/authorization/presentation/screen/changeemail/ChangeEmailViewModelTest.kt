package me.bookk.feature.authorization.presentation.screen.changeemail

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CompletableDeferred
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.authorization.resources.AuthRes
import me.bookk.core.domain.entity.Error
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
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.feature.authorization.domain.api.StartEmailChange
import me.bookk.feature.authorization.domain.entity.EmailVerification
import me.bookk.feature.authorization.domain.entity.EmailVerificationError
import me.bookk.feature.authorization.presentation.FakeAuthStateFactory
import me.bookk.feature.authorization.presentation.FakeValidateEmail
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlin.uuid.Uuid

class ChangeEmailViewModelTest {

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
        val startEmailChange = mock<StartEmailChange>()
        val errorMapper = FakeErrorMapper()
        val pendingEmailChange = StartEmailChange.PendingEmailChange(
            email = "new@example.com",
            verification = EmailVerification(
                id = Uuid.parse("00000000-0000-0000-0000-0000000000d4"),
                expiresAt = Instant.fromEpochSeconds(1_000),
                resendAvailableAt = Instant.fromEpochSeconds(100),
            ),
        )

        fun sut(): ChangeEmailViewModel {
            return ChangeEmailViewModel(
                startEmailChange = startEmailChange,
                validateEmail = FakeValidateEmail(),
                stateFactory = FakeAuthStateFactory(),
                vmArgs = VmArgs(errorMapper),
            )
        }

        fun stubStarted() {
            everySuspend { startEmailChange(any()) } returns pendingEmailChange
        }

        fun stubRefused(error: Throwable) {
            everySuspend { startEmailChange(any()) } throws error
        }
    }

    private val ChangeEmailViewModel.email: FakeTextFieldState
        get() = uiState.email as FakeTextFieldState

    private fun ChangeEmailViewModel.submit(address: String = "new@example.com") {
        email.type(address)
        uiState.continueButton.onClick?.invoke()
    }

    private fun ChangeEmailViewModel.assertEmailError(expected: StringDesc) {
        assertEquals(expected, uiState.email.supportingTextRes)
        assertEquals(ValidationState.ERROR, uiState.email.validationState)
    }

    @Test
    fun `asks for the new address`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(AuthRes.strings.change_email_title.desc(), sut.uiState.appBar.title)
        assertEquals(AuthRes.strings.change_email_explanation.desc(), sut.uiState.explanation)
        assertEquals(AuthRes.strings.change_email_field_label.desc(), sut.uiState.email.label)
        assertEquals(InputType.EMAIL, sut.uiState.email.inputType)
        assertFalse(sut.uiState.continueButton.isEnabled)
    }

    @Test
    fun `enables continue once an address is typed`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.email.type("n")

        then()
        assertTrue(sut.uiState.continueButton.isEnabled)
    }

    @Test
    fun `disables continue when the address is cleared`() = runUnitTest {
        given()
        val sut = Fixture().sut()
        sut.email.type("new@example.com")

        whenn()
        sut.email.type("  ")

        then()
        assertFalse(sut.uiState.continueButton.isEnabled)
    }

    @Test
    fun `shows a format error without requesting a code`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.submit("not-an-email")

        then()
        sut.assertEmailError(AuthRes.strings.sign_up_email_error.desc())
        verifySuspend(VerifyMode.not) { fixture.startEmailChange(any()) }
    }

    @Test
    fun `requests a code for the typed address`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubStarted()
        val sut = fixture.sut()

        whenn()
        sut.submit("new@example.com")

        then()
        verifySuspend { fixture.startEmailChange("new@example.com") }
    }

    @Test
    fun `opens code verification once the code is sent`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubStarted()
        val sut = fixture.sut()

        whenn()
        sut.submit()

        then()
        val destination = assertIs<ChangeEmailDestinations.VerifyEmail>(
            sut.uiState.navigation.navigationDestination.single(),
        )
        assertSame(fixture.pendingEmailChange, destination.pendingEmailChange)
    }

    @Test
    fun `shows a taken address inline`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRefused(StartEmailChange.Error.EmailAlreadyExist())
        val sut = fixture.sut()

        whenn()
        sut.submit()

        then()
        sut.assertEmailError(AuthRes.strings.sign_up_email_exist.desc())
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
        assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
    }

    @Test
    fun `shows an address refused by the server inline`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRefused(StartEmailChange.Error.InvalidEmailFormat())
        val sut = fixture.sut()

        whenn()
        sut.submit()

        then()
        sut.assertEmailError(AuthRes.strings.sign_up_email_error.desc())
    }

    @Test
    fun `shows an undeliverable address inline`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRefused(EmailVerificationError.Undeliverable())
        val sut = fixture.sut()

        whenn()
        sut.submit()

        then()
        sut.assertEmailError(AuthRes.strings.sign_up_email_undeliverable.desc())
    }

    @Test
    fun `explains a code sent too recently inline`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRefused(EmailVerificationError.ResendTooSoon())
        val sut = fixture.sut()

        whenn()
        sut.submit()

        then()
        sut.assertEmailError(AuthRes.strings.verify_email_resend_too_soon.desc())
    }

    @Test
    fun `explains rate limiting inline`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRefused(Error.TooManyRequests(Exception()))
        val sut = fixture.sut()

        whenn()
        sut.submit()

        then()
        sut.assertEmailError(DesignSystem.strings.error_too_many_requests.desc())
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
    }

    @Test
    fun `shows mapped error when the request fails unexpectedly`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRefused(TestException())
        val sut = fixture.sut()

        whenn()
        sut.submit()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertEquals(ValidationState.DEFAULT, sut.uiState.email.validationState)
    }

    @Test
    fun `keeps continue enabled after the request fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRefused(TestException())
        val sut = fixture.sut()

        whenn()
        sut.submit()

        then()
        assertTrue(sut.uiState.continueButton.isEnabled)
        assertFalse(sut.uiState.continueButton.isLoading)
    }

    @Test
    fun `clears the address error once the address is edited`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRefused(StartEmailChange.Error.EmailAlreadyExist())
        val sut = fixture.sut()
        sut.submit()

        whenn()
        sut.email.type("other@example.com")

        then()
        assertNull(sut.uiState.email.supportingTextRes)
        assertEquals(ValidationState.DEFAULT, sut.uiState.email.validationState)
    }

    @Test
    fun `does not request a second code while one is being sent`() = runUnitTest {
        given()
        val fixture = Fixture()
        val started = CompletableDeferred<StartEmailChange.PendingEmailChange>()
        everySuspend { fixture.startEmailChange(any()) } calls { started.await() }
        val sut = fixture.sut()
        sut.submit()

        whenn()
        sut.uiState.continueButton.onClick?.invoke()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.startEmailChange(any()) }
        assertTrue(sut.uiState.continueButton.isLoading)
        started.complete(fixture.pendingEmailChange)
    }

    @Test
    fun `goes back`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(
            listOf<ChangeEmailDestinations>(ChangeEmailDestinations.Back),
            sut.uiState.navigation.navigationDestination,
        )
    }
}
