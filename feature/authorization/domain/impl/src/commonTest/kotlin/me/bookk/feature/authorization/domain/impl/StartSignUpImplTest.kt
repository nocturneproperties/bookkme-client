package me.bookk.feature.authorization.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matches
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import me.bookk.core.domain.entity.Error
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.StartSignUp
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.registration.RegistrationDataSource
import me.bookk.feature.authorization.domain.datasource.registration.SignUpSession
import me.bookk.feature.authorization.domain.entity.AcceptedLegalDocuments
import me.bookk.feature.authorization.domain.entity.EmailVerificationError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Instant

class StartSignUpImplTest {

    private class Fixture {
        val registrationDataSource = mock<RegistrationDataSource>()
        val sessionDataSource = InMemorySignUpSessionDataSource()
        val sut = StartSignUpImpl(registrationDataSource, sessionDataSource)
    }

    private fun Fixture.stubChallenge() {
        everySuspend { registrationDataSource.getSignUpPasskeyChallenge(any()) } returns
            stubSignUpChallenge()
    }

    private fun Fixture.stubChallengeError(code: Int) {
        everySuspend { registrationDataSource.getSignUpPasskeyChallenge(any()) } throws
            Error.BusinessError(code, "rejected")
    }

    @Test
    fun `returns masked email and the verification times`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubChallenge()

        whenn()
        val result = fixture.sut(stubSignUpUserData())

        then()
        assertEquals("j***@example.com", result.maskedEmail)
        assertEquals(Instant.fromEpochSeconds(1_000), result.expiresAt)
        assertEquals(Instant.fromEpochSeconds(100), result.resendAvailableAt)
    }

    @Test
    fun `masks a single character local part`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubChallenge()

        whenn()
        val result = fixture.sut(StartSignUp.UserData("John", "Doe", "j@example.com"))

        then()
        assertEquals("j***@example.com", result.maskedEmail)
    }

    @Test
    fun `requests the challenge with trimmed user data`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubChallenge()

        whenn()
        fixture.sut(StartSignUp.UserData("  John ", "Doe\n", " john@example.com "))

        then()
        verifySuspend {
            fixture.registrationDataSource.getSignUpPasskeyChallenge(
                matches({ "trimmed" }) {
                    it.firstName == "John" && it.lastName == "Doe" &&
                        it.email == "john@example.com"
                },
            )
        }
    }

    @Test
    fun `stores the session without an attestation`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubChallenge()
        val documents = AcceptedLegalDocuments("privacy-v3", "terms-v2")

        whenn()
        fixture.sut(StartSignUp.UserData(" John ", "Doe", "john@example.com", documents))

        then()
        val session = fixture.sessionDataSource.session!!
        assertEquals("John", session.userData.firstName)
        assertEquals(documents, session.userData.acceptedDocuments)
        assertEquals("req-id", session.challenge.requestId)
        assertNull(session.attestation)
    }

    @Test
    fun `replaces the previous session when started again`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubChallenge()
        fixture.sessionDataSource.save(
            SignUpSession(stubSignUpUserData(), stubSignUpChallenge()),
        )

        whenn()
        fixture.sut(StartSignUp.UserData("Jane", "Doe", "jane@example.com"))

        then()
        assertEquals("Jane", fixture.sessionDataSource.session!!.userData.firstName)
    }

    @Test
    fun `maps challenge error 1 to EmailAlreadyExist`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubChallengeError(AuthErrorCodes.EMAIL_EXIST)

        whenn()
        val result = runCatching { fixture.sut(stubSignUpUserData()) }

        then()
        assertFailsWith<StartSignUp.Error.EmailAlreadyExist> { result.getOrThrow() }
    }

    @Test
    fun `maps challenge error 2 to InvalidEmailFormat`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubChallengeError(AuthErrorCodes.INVALID_EMAIL_FORMAT)

        whenn()
        val result = runCatching { fixture.sut(stubSignUpUserData()) }

        then()
        assertFailsWith<StartSignUp.Error.InvalidEmailFormat> { result.getOrThrow() }
    }

    @Test
    fun `maps challenge error 910005 to Undeliverable`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubChallengeError(AuthErrorCodes.EMAIL_UNDELIVERABLE)

        whenn()
        val result = runCatching { fixture.sut(stubSignUpUserData()) }

        then()
        assertFailsWith<EmailVerificationError.Undeliverable> { result.getOrThrow() }
    }

    @Test
    fun `maps challenge error 910004 to ResendTooSoon`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubChallengeError(AuthErrorCodes.EMAIL_CODE_RESEND_TOO_SOON)

        whenn()
        val result = runCatching { fixture.sut(stubSignUpUserData()) }

        then()
        assertFailsWith<EmailVerificationError.ResendTooSoon> { result.getOrThrow() }
    }

    @Test
    fun `rethrows unrelated business errors`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubChallengeError(777)

        whenn()
        val result = runCatching { fixture.sut(stubSignUpUserData()) }

        then()
        assertEquals(777, assertFailsWith<Error.BusinessError> { result.getOrThrow() }.errorCode)
    }

    @Test
    fun `rethrows too many requests`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.registrationDataSource.getSignUpPasskeyChallenge(any()) } throws
            Error.TooManyRequests(Exception())

        whenn()
        val result = runCatching { fixture.sut(stubSignUpUserData()) }

        then()
        assertFailsWith<Error.TooManyRequests> { result.getOrThrow() }
    }

    @Test
    fun `keeps the previous session when the challenge fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubChallengeError(AuthErrorCodes.EMAIL_CODE_RESEND_TOO_SOON)
        val previous = SignUpSession(stubSignUpUserData(), stubSignUpChallenge())
        fixture.sessionDataSource.save(previous)

        whenn()
        runCatching { fixture.sut(stubSignUpUserData()) }

        then()
        assertEquals(previous, fixture.sessionDataSource.session)
    }
}
