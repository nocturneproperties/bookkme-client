package me.bookk.feature.authorization.presentation.screen.verifyemail

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import me.bookk.android.feature.authorization.resources.AuthRes
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
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.feature.authorization.domain.api.ConfirmEmailChange
import me.bookk.feature.authorization.domain.api.FinishSignUp
import me.bookk.feature.authorization.domain.api.ResendSignUpCode
import me.bookk.feature.authorization.domain.api.StartEmailChange
import me.bookk.feature.authorization.domain.entity.EmailVerification
import me.bookk.feature.authorization.domain.entity.EmailVerificationError
import me.bookk.feature.authorization.presentation.FakeAuthStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlin.uuid.Uuid

class VerifyEmailChangeViewModelTest {

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
        val startEmailChange = mock<StartEmailChange>()
        val confirmEmailChange = mock<ConfirmEmailChange>()
        val errorMapper = FakeErrorMapper()
        val clock = object : Clock {
            override fun now(): Instant {
                return start + scheduler.currentTime.milliseconds
            }
        }
        val pendingEmailChange = pendingEmailChange("00000000-0000-0000-0000-0000000000e1")

        fun pendingEmailChange(verificationId: String): StartEmailChange.PendingEmailChange {
            return StartEmailChange.PendingEmailChange(
                email = "new@example.com",
                verification = EmailVerification(
                    id = Uuid.parse(verificationId),
                    expiresAt = start + 10.minutes,
                    resendAvailableAt = clock.now() + 60.seconds,
                ),
            )
        }

        fun sut(): VerifyEmailViewModel {
            return VerifyEmailViewModel(
                initialRequest = VerifyEmailRequest.EmailChange(pendingEmailChange),
                finishSignUp = finishSignUp,
                resendSignUpCode = resendSignUpCode,
                startEmailChange = startEmailChange,
                confirmEmailChange = confirmEmailChange,
                clock = clock,
                stateFactory = FakeAuthStateFactory(),
                vmArgs = VmArgs(errorMapper),
            )
        }

        fun stubConfirmed() {
            everySuspend { confirmEmailChange(any(), any()) } returns Unit
        }

        fun stubConfirmRefused(error: Throwable) {
            everySuspend { confirmEmailChange(any(), any()) } throws error
        }
    }

    private val VerifyEmailViewModel.code: FakeTextFieldState
        get() = uiState.code as FakeTextFieldState

    private fun VerifyEmailViewModel.assertCodeError(expected: StringDesc) {
        assertEquals(expected, uiState.code.supportingTextRes)
        assertEquals(ValidationState.ERROR, uiState.code.validationState)
    }

    private fun TestScope.resendAfterCountdown(sut: VerifyEmailViewModel) {
        advanceTimeBy(60.seconds)
        runCurrent()
        sut.uiState.resendButton.onClick?.invoke()
    }

    @Test
    fun `describes the new address the code was sent to`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(
            AuthRes.strings.verify_email_change_description.format("new@example.com"),
            sut.uiState.codeSentMessage,
        )
    }

    @Test
    fun `confirms the email change with the typed code`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        fixture.stubConfirmed()
        val sut = fixture.sut()

        whenn()
        sut.code.type("123456")

        then()
        verifySuspend { fixture.confirmEmailChange(fixture.pendingEmailChange, "123456") }
    }

    @Test
    fun `plays success haptic when the email change is confirmed`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        fixture.stubConfirmed()
        val sut = fixture.sut()

        whenn()
        sut.code.type("123456")

        then()
        assertEquals(
            1,
            sut.uiState.notifications.presentationNotification.count {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `does not play success haptic when the email change fails`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        fixture.stubConfirmRefused(TestException())
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
    fun `announces the changed email and leaves the flow`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        fixture.stubConfirmed()
        val sut = fixture.sut()

        whenn()
        sut.code.type("123456")

        then()
        val message = sut.uiState.notifications.presentationNotification
            .filterIsInstance<PresentationNotification.GlobalMessage>()
            .single()
        assertEquals(AuthRes.strings.verify_email_change_success.desc(), message.text)
        assertEquals(PresentationNotification.GlobalMessage.State.SUCCESS, message.state)
        assertEquals(
            listOf<VerifyEmailDestinations>(VerifyEmailDestinations.EmailChanged),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `clears the code and explains a wrong code`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        fixture.stubConfirmRefused(EmailVerificationError.CodeInvalid())
        val sut = fixture.sut()

        whenn()
        sut.code.type("123456")

        then()
        assertEquals("", sut.uiState.code.text)
        sut.assertCodeError(AuthRes.strings.verify_email_code_invalid.desc())
        assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
    }

    @Test
    fun `keeps the code and explains an address taken meanwhile`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        fixture.stubConfirmRefused(ConfirmEmailChange.Error.EmailAlreadyExist())
        val sut = fixture.sut()

        whenn()
        sut.code.type("123456")

        then()
        assertEquals("123456", sut.uiState.code.text)
        sut.assertCodeError(AuthRes.strings.verify_email_change_email_taken.desc())
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
        assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
    }

    @Test
    fun `requests a new code for the same address`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        val sut = fixture.sut()
        everySuspend { fixture.startEmailChange(any()) } calls {
            fixture.pendingEmailChange("00000000-0000-0000-0000-0000000000e2")
        }

        whenn()
        resendAfterCountdown(sut)

        then()
        verifySuspend { fixture.startEmailChange("new@example.com") }
        assertFalse(sut.uiState.resendButton.isEnabled)
        assertEquals(
            AuthRes.strings.verify_email_resend_countdown.format("1:00"),
            sut.uiState.resendButton.text,
        )
    }

    @Test
    fun `confirms with the newest verification after a resend`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        val sut = fixture.sut()
        val resent = fixture.pendingEmailChange("00000000-0000-0000-0000-0000000000e2")
        everySuspend { fixture.startEmailChange(any()) } returns resent
        resendAfterCountdown(sut)
        fixture.stubConfirmed()

        whenn()
        sut.code.type("654321")

        then()
        verifySuspend { fixture.confirmEmailChange(resent, "654321") }
    }

    @Test
    fun `explains an address refused on resend inline`() = runUnitTest {
        listOf(
            StartEmailChange.Error.EmailAlreadyExist() to AuthRes.strings.verify_email_change_email_taken.desc(),
            StartEmailChange.Error.InvalidEmailFormat() to AuthRes.strings.sign_up_email_error.desc(),
            EmailVerificationError.Undeliverable() to AuthRes.strings.sign_up_email_undeliverable.desc(),
        ).forEach { (error, expected) ->
            given()
            val fixture = Fixture(testScheduler)
            everySuspend { fixture.startEmailChange(any()) } throws error
            val sut = fixture.sut()

            whenn()
            resendAfterCountdown(sut)

            then()
            sut.assertCodeError(expected)
            assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
            assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
        }
    }

    @Test
    fun `explains a code sent too recently inline`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        everySuspend { fixture.startEmailChange(any()) } throws EmailVerificationError.ResendTooSoon()
        val sut = fixture.sut()

        whenn()
        resendAfterCountdown(sut)

        then()
        sut.assertCodeError(AuthRes.strings.verify_email_resend_too_soon.desc())
    }

    @Test
    fun `goes back to change the address`() = runUnitTest {
        given()
        val sut = Fixture(testScheduler).sut()

        whenn()
        sut.uiState.changeEmailButton.onClick?.invoke()

        then()
        assertEquals(
            listOf<VerifyEmailDestinations>(VerifyEmailDestinations.Back),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `does not leave the flow when the code is refused`() = runUnitTest {
        given()
        val fixture = Fixture(testScheduler)
        fixture.stubConfirmRefused(EmailVerificationError.CodeExpired())
        val sut = fixture.sut()

        whenn()
        sut.code.type("123456")

        then()
        sut.assertCodeError(AuthRes.strings.verify_email_code_expired.desc())
        assertTrue(
            sut.uiState.notifications.presentationNotification.none {
                it is PresentationNotification.GlobalMessage
            },
        )
    }
}
