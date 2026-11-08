package me.bookk.feature.authorization.domain.impl

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
import me.bookk.core.domain.entity.Error
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.ResendSignUpCode
import me.bookk.feature.authorization.domain.api.StartSignUp
import me.bookk.feature.authorization.domain.datasource.registration.SignUpSession
import me.bookk.feature.authorization.domain.entity.EmailVerificationError
import me.bookk.feature.authorization.domain.entity.SignUpRejection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.time.Instant

class ResendSignUpCodeImplTest {

    private class Fixture {
        val startSignUp = mock<StartSignUp>()
        val sessionDataSource = InMemorySignUpSessionDataSource()
        val userData = Capture.slot<StartSignUp.UserData>()
        val pendingSignUp = StartSignUp.PendingSignUp(
            maskedEmail = "j***@example.com",
            expiresAt = Instant.fromEpochSeconds(2_000),
            resendAvailableAt = Instant.fromEpochSeconds(200),
        )
        val sut = ResendSignUpCodeImpl(startSignUp, sessionDataSource)
    }

    private fun Fixture.startSession(session: SignUpSession = activeSession()) {
        sessionDataSource.save(session)
    }

    private fun activeSession(): SignUpSession {
        return SignUpSession(stubSignUpUserData(), stubSignUpChallenge())
    }

    private fun Fixture.failStartWith(error: Throwable) {
        startSession()
        everySuspend { startSignUp(any()) } throws error
    }

    @Test
    fun `restarts sign up with the stored user data`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.startSession()
        everySuspend { fixture.startSignUp(capture(fixture.userData)) } returns
            fixture.pendingSignUp

        whenn()
        val result = fixture.sut()

        then()
        assertSame(fixture.pendingSignUp, result)
        assertEquals(stubSignUpUserData().email, fixture.userData.get().email)
    }

    @Test
    fun `restarts sign up once the challenge was dropped`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.startSession(SignUpSession(stubSignUpUserData(), challenge = null))
        everySuspend { fixture.startSignUp(any()) } returns fixture.pendingSignUp

        whenn()
        val result = fixture.sut()

        then()
        assertSame(fixture.pendingSignUp, result)
    }

    @Test
    fun `throws SignUpNotStarted without a session`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<ResendSignUpCode.Error.SignUpNotStarted> { result.getOrThrow() }
        verifySuspend(VerifyMode.not) { fixture.startSignUp(any()) }
    }

    @Test
    fun `rejects the email when it is already taken`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failStartWith(StartSignUp.Error.EmailAlreadyExist())

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<StartSignUp.Error.EmailAlreadyExist> { result.getOrThrow() }
        assertRejected(fixture, SignUpRejection.EMAIL_TAKEN)
    }

    @Test
    fun `rejects the email when its format is invalid`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failStartWith(StartSignUp.Error.InvalidEmailFormat())

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<StartSignUp.Error.InvalidEmailFormat> { result.getOrThrow() }
        assertRejected(fixture, SignUpRejection.EMAIL_INVALID)
    }

    @Test
    fun `rejects the email when it is undeliverable`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failStartWith(EmailVerificationError.Undeliverable())

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<EmailVerificationError.Undeliverable> { result.getOrThrow() }
        assertRejected(fixture, SignUpRejection.EMAIL_UNDELIVERABLE)
    }

    @Test
    fun `keeps the session when a resend is too soon`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failStartWith(EmailVerificationError.ResendTooSoon())

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<EmailVerificationError.ResendTooSoon> { result.getOrThrow() }
        assertEquals(
            activeSession().userData.email,
            fixture.sessionDataSource.session?.userData?.email,
        )
        assertNotNull(fixture.sessionDataSource.session?.challenge)
        assertNull(fixture.sessionDataSource.session?.rejection)
    }

    @Test
    fun `keeps the session when the connection fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failStartWith(Error.NoConnectionError(Exception()))

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<Error.NoConnectionError> { result.getOrThrow() }
        assertNotNull(fixture.sessionDataSource.session?.challenge)
        assertNull(fixture.sessionDataSource.session?.rejection)
    }

    private fun assertRejected(fixture: Fixture, rejection: SignUpRejection) {
        val session = assertNotNull(fixture.sessionDataSource.session)
        assertEquals(stubSignUpUserData().email, session.userData.email)
        assertNull(session.challenge)
        assertEquals(rejection, session.rejection)
    }
}
