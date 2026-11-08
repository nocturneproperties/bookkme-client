package me.bookk.feature.authorization.presentation.screen.verifyemail

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentially
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.authorization.resources.AuthRes
import me.bookk.core.domain.entity.Error
import me.bookk.core.presentation.VmArgs
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
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.feature.authorization.domain.api.FinishSignUp
import me.bookk.feature.authorization.domain.api.ResendSignUpCode
import me.bookk.feature.authorization.domain.api.StartSignUp
import me.bookk.feature.authorization.domain.entity.EmailVerificationError
import me.bookk.feature.authorization.presentation.FakeAuthStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class VerifyEmailViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private class Fixture(private val scheduler: TestCoroutineScheduler) {
        val start = Instant.parse("2026-11-08T12:00:00Z")
        val finishSignUp = mock<FinishSignUp>()
        val resendSignUpCode = mock<ResendSignUpCode>()
        val errorMapper = FakeErrorMapper()
        val clock = object : Clock {
            override fun now(): Instant {
                return start + scheduler.currentTime.milliseconds
            }
        }

        fun pendingSignUp(resendAvailableAt: Instant = start + 60.seconds): StartSignUp.PendingSignUp {
            return StartSignUp.PendingSignUp(
                maskedEmail = "a***@example.com",
                expiresAt = start + 10.minutes,
                resendAvailableAt = resendAvailableAt,
            )
        }

        fun sut(): VerifyEmailViewModel {
            return VerifyEmailViewModel(
                pendingSignUp = pendingSignUp(),
                finishSignUp = finishSignUp,
                resendSignUpCode = resendSignUpCode,
                clock = clock,
                stateFactory = FakeAuthStateFactory(),
                vmArgs = VmArgs(errorMapper),
            )
        }
    }

    private val VerifyEmailViewModel.code: FakeTextFieldState
        get() = uiState.code as FakeTextFieldState

    private fun Fixture.failFinishWith(error: Throwable) {
        everySuspend { finishSignUp(any()) } throws error
    }

    private fun VerifyEmailViewModel.assertCodeError(expected: StringDesc) {
        assertEquals(expected, uiState.code.supportingTextRes)
        assertEquals(ValidationState.ERROR, uiState.code.validationState)
    }

    @Test
    fun `describes where the code was sent`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(
            AuthRes.strings.verify_email_description.format("a***@example.com"),
            sut.uiState.codeSentMessage,
        )
    }

    @Test
    fun `keeps only the first six digits of the typed code`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        everySuspend { fixture.finishSignUp(any()) } returns Unit
        val sut = fixture.sut()

        whenn()
        sut.code.type("12a3 4-5678")

        then()
        assertEquals("123456", sut.uiState.code.text)
    }

    @Test
    fun `enables confirm only for a complete code`() = runUnitTest {
        given()
        val sut = Fixture(testScheduler).sut()

        whenn()
        sut.code.type("12345")

        then()
        assertFalse(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `submits the code automatically once six digits are typed`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        everySuspend { fixture.finishSignUp(any()) } returns Unit
        val sut = fixture.sut()

        whenn()
        sut.code.type("123456")

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.finishSignUp("123456") }
    }

    @Test
    fun `submits the code again on confirm after a connection failure`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        everySuspend { fixture.finishSignUp(any()) } sequentially {
            throws(Error.NoConnectionError(Exception()))
            returns(Unit)
        }
        val sut = fixture.sut()
        sut.code.type("123456")

        whenn()
        sut.uiState.confirmButton.onClick?.invoke()

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.finishSignUp("123456") }
        assertTrue(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `plays success haptic when the email is verified`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        everySuspend { fixture.finishSignUp(any()) } returns Unit
        val sut = fixture.sut()

        whenn()
        sut.code.type("123456")

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.SuccessHaptic>()
    }

    @Test
    fun `does not play success haptic when verification fails`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        fixture.failFinishWith(EmailVerificationError.CodeInvalid())
        val sut = fixture.sut()

        whenn()
        sut.code.type("123456")

        then()
        assertTrue(
            sut.uiState.notifications.presentationNotification.none {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `clears the code and explains a wrong code`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        fixture.failFinishWith(EmailVerificationError.CodeInvalid())
        val sut = fixture.sut()

        whenn()
        sut.code.type("123456")

        then()
        assertEquals("", sut.uiState.code.text)
        sut.assertCodeError(AuthRes.strings.verify_email_code_invalid.desc())
        assertFalse(sut.uiState.confirmButton.isEnabled)
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `clears the wrong code error once a new code is typed`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        fixture.failFinishWith(EmailVerificationError.CodeInvalid())
        val sut = fixture.sut()
        sut.code.type("123456")

        whenn()
        sut.code.type("1")

        then()
        assertNull(sut.uiState.code.supportingTextRes)
        assertEquals(ValidationState.DEFAULT, sut.uiState.code.validationState)
    }

    @Test
    fun `asks for a new code once the verification expired`() = runUnitTest {
        listOf(
            EmailVerificationError.CodeExpired(),
            FinishSignUp.Error.ChallengeWindowExpired(),
            FinishSignUp.Error.SignUpNotStarted(),
        ).forEach { error ->
            given()
            val fixture = Fixture(testScheduler)
            fixture.failFinishWith(error)
            val sut = fixture.sut()

            whenn()
            sut.code.type("123456")

            then()
            sut.assertCodeError(AuthRes.strings.verify_email_code_expired.desc())
            assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
        }
    }

    @Test
    fun `asks for a new code after too many wrong attempts`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        fixture.failFinishWith(EmailVerificationError.AttemptsExceeded())
        val sut = fixture.sut()

        whenn()
        sut.code.type("123456")

        then()
        sut.assertCodeError(AuthRes.strings.verify_email_attempts_exceeded.desc())
    }

    @Test
    fun `disables confirm once the code can no longer be verified`() = runUnitTest {
        listOf(
            EmailVerificationError.CodeExpired(),
            EmailVerificationError.AttemptsExceeded(),
            FinishSignUp.Error.ChallengeWindowExpired(),
            FinishSignUp.Error.SignUpNotStarted(),
            FinishSignUp.Error.PasskeyVerificationFailed(),
            FinishSignUp.Error.AccountCreationFailed(),
        ).forEach { error ->
            given()
            val fixture = Fixture(testScheduler)
            fixture.failFinishWith(error)
            val sut = fixture.sut()

            whenn()
            sut.code.type("123456")

            then()
            assertEquals("", sut.uiState.code.text)
            assertFalse(sut.uiState.confirmButton.isEnabled)
        }
    }

    @Test
    fun `explains rate limiting of the code check inline`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        fixture.failFinishWith(Error.TooManyRequests(Exception()))
        val sut = fixture.sut()

        whenn()
        sut.code.type("123456")

        then()
        sut.assertCodeError(DesignSystem.strings.error_too_many_requests.desc())
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
    }

    @Test
    fun `shows a message when the passkey step fails`() = runUnitTest {
        listOf(
            FinishSignUp.Error.PasskeyVerificationFailed() to AuthRes.strings.sign_up_passkey_failed,
            FinishSignUp.Error.AccountCreationFailed() to AuthRes.strings.sign_up_failed,
        ).forEach { (error, expected) ->
            given()
            val fixture = Fixture(testScheduler)
            fixture.failFinishWith(error)
            val sut = fixture.sut()

            whenn()
            sut.code.type("123456")

            then()
            assertEquals(
                expected.desc(),
                sut.uiState.notifications.assertSingle<PresentationNotification.Message>().message,
            )
        }
    }

    @Test
    fun `returns to the form when the sign up details are rejected`() = runUnitTest {
        listOf(
            FinishSignUp.Error.EmailAlreadyExist(),
            FinishSignUp.Error.InvalidEmailFormat(),
            FinishSignUp.Error.LegalDocumentVersionUnknown(),
            FinishSignUp.Error.LegalDocumentsNotAccepted(),
        ).forEach { error ->
            given()
            val fixture = Fixture(testScheduler)
            fixture.failFinishWith(error)
            val sut = fixture.sut()

            whenn()
            sut.code.type("123456")

            then()
            assertEquals(
                listOf<VerifyEmailDestinations>(VerifyEmailDestinations.Back),
                sut.uiState.navigation.navigationDestination,
            )
            assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
        }
    }

    @Test
    fun `shows mapped error when verification fails unexpectedly`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        fixture.failFinishWith(TestException())
        val sut = fixture.sut()

        whenn()
        sut.code.type("123456")

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `does not submit a second time while verifying`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        val finished = CompletableDeferred<Unit>()
        everySuspend { fixture.finishSignUp(any()) } calls { finished.await() }
        val sut = fixture.sut()
        sut.code.type("123456")

        whenn()
        sut.uiState.confirmButton.onClick?.invoke()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.finishSignUp(any()) }
        assertTrue(sut.uiState.confirmButton.isLoading)
        finished.complete(Unit)
    }

    @Test
    fun `counts down until a new code can be requested`() = runUnitTest {
        given()
        val sut = Fixture(testScheduler).sut()

        whenn()
        advanceTimeBy(15.seconds)
        runCurrent()

        then()
        assertFalse(sut.uiState.resendButton.isEnabled)
        assertEquals(
            AuthRes.strings.verify_email_resend_countdown.format("0:45"),
            sut.uiState.resendButton.text,
        )
    }

    @Test
    fun `shows the full wait before the countdown starts`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.resendButton.isEnabled)
        assertEquals(
            AuthRes.strings.verify_email_resend_countdown.format("1:00"),
            sut.uiState.resendButton.text,
        )
    }

    @Test
    fun `enables resend once the countdown ends`() = runUnitTest {
        given()
        val sut = Fixture(testScheduler).sut()

        whenn()
        advanceTimeBy(60.seconds)
        runCurrent()

        then()
        assertTrue(sut.uiState.resendButton.isEnabled)
        assertEquals(AuthRes.strings.verify_email_resend_button.desc(), sut.uiState.resendButton.text)
    }

    @Test
    fun `requests a new code and restarts the countdown`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        fixture.failFinishWith(EmailVerificationError.CodeExpired())
        val sut = fixture.sut()
        sut.code.type("123456")
        advanceTimeBy(60.seconds)
        runCurrent()
        everySuspend { fixture.resendSignUpCode() } returns
            fixture.pendingSignUp(resendAvailableAt = fixture.clock.now() + 60.seconds)

        whenn()
        sut.uiState.resendButton.onClick?.invoke()

        then()
        assertFalse(sut.uiState.resendButton.isEnabled)
        assertFalse(sut.uiState.resendButton.isLoading)
        assertEquals(
            AuthRes.strings.verify_email_resend_countdown.format("1:00"),
            sut.uiState.resendButton.text,
        )
        assertEquals("", sut.uiState.code.text)
        assertNull(sut.uiState.code.supportingTextRes)
    }

    @Test
    fun `explains a refused resend inline`() = runUnitTest {
        listOf(
            EmailVerificationError.ResendTooSoon() to AuthRes.strings.verify_email_resend_too_soon.desc(),
            Error.TooManyRequests(Exception()) to DesignSystem.strings.error_too_many_requests.desc(),
        ).forEach { (error, expected) ->
            given()
            val fixture = Fixture(testScheduler)
            everySuspend { fixture.resendSignUpCode() } throws error
            val sut = fixture.sut()
            advanceTimeBy(60.seconds)
            runCurrent()

            whenn()
            sut.uiState.resendButton.onClick?.invoke()

            then()
            sut.assertCodeError(expected)
            assertTrue(sut.uiState.resendButton.isEnabled)
            assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
        }
    }

    @Test
    fun `returns to the form when the email is rejected on resend`() = runUnitTest {
        listOf(
            StartSignUp.Error.EmailAlreadyExist(),
            StartSignUp.Error.InvalidEmailFormat(),
            EmailVerificationError.Undeliverable(),
            ResendSignUpCode.Error.SignUpNotStarted(),
        ).forEach { error ->
            given()
            val fixture = Fixture(testScheduler)
            everySuspend { fixture.resendSignUpCode() } throws error
            val sut = fixture.sut()
            advanceTimeBy(60.seconds)
            runCurrent()

            whenn()
            sut.uiState.resendButton.onClick?.invoke()

            then()
            assertEquals(
                listOf<VerifyEmailDestinations>(VerifyEmailDestinations.Back),
                sut.uiState.navigation.navigationDestination,
            )
            assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
        }
    }

    @Test
    fun `shows mapped error when resend fails unexpectedly`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        everySuspend { fixture.resendSignUpCode() } throws TestException()
        val sut = fixture.sut()
        advanceTimeBy(60.seconds)
        runCurrent()

        whenn()
        sut.uiState.resendButton.onClick?.invoke()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `does not request a new code while a code is being verified`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        val finished = CompletableDeferred<Unit>()
        everySuspend { fixture.finishSignUp(any()) } calls { finished.await() }
        val sut = fixture.sut()
        advanceTimeBy(60.seconds)
        runCurrent()
        sut.code.type("123456")

        whenn()
        sut.uiState.resendButton.onClick?.invoke()

        then()
        verifySuspend(VerifyMode.not) { fixture.resendSignUpCode() }
        finished.complete(Unit)
    }

    @Test
    fun `goes back to change the email`() = runUnitTest {
        given()
        val sut = Fixture(testScheduler).sut()

        whenn()
        sut.uiState.changeEmailButton.onClick?.invoke()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(
            listOf<VerifyEmailDestinations>(VerifyEmailDestinations.Back, VerifyEmailDestinations.Back),
            sut.uiState.navigation.navigationDestination,
        )
    }
}
